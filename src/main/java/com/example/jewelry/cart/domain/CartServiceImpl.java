package com.example.jewelry.cart.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.cart.dto.AddToCartRequest;
import com.example.jewelry.cart.dto.CartItemResponse; // Import
import com.example.jewelry.cart.dto.CartResponse;     // Import
import com.example.jewelry.cart.web.CartService;
import com.example.jewelry.product.domain.Product;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.product.domain.ProductVariant;
import com.example.jewelry.product.domain.ProductVariantRepository;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public CartResponse getMyCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        ProductVariant variant = variantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể"));

        if(!variant.getProduct().getId().equals(request.getProductId())) {
            throw new RuntimeException("Biến thể không hợp lệ");
        }



        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));


        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) && item.getVariant().getId().equals(variant.getId())).findFirst();


        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            // Check kho của Variant
            if (newQuantity > variant.getStockQuantity()) {
                throw new RuntimeException("Vượt quá số lượng tồn kho của phân loại này!");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            if (request.getQuantity() > variant.getStockQuantity()) {
                throw new RuntimeException("Vượt quá số lượng tồn kho!");
            }
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(UUID userId, UUID cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CART_EMPTY));

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart); // <--- Map sang DTO
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(UUID userId, UUID cartItemId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new DomainException(DomainExceptionCode.CART_EMPTY));

        CartItem item = cart.getItems().stream().filter(item1 -> item1.getId().equals(cartItemId)).findFirst().orElseThrow(() -> new DomainException(DomainExceptionCode.CART_ITEM_NOT_FOUND)); ;

        if(quantity <= 0){
            cart.getItems().remove(item);
        }else {
            item.setQuantity(quantity);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);

    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

    // --- Helper Methods ---

    private Cart createNewCart(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.USER_NOT_FOUND));
        Cart newCart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(newCart);
    }

    private CartItemResponse mapCartItemToDto(CartItem item) {
        CartItemResponse dto = new CartItemResponse();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        // Thông tin Product gốc
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setProductImage(item.getProduct().getMainImageUrl());
        }

        // --- BỔ SUNG LOGIC LẤY THÔNG TIN VARIANT VÀ TÍNH LẠI GIÁ ---
        BigDecimal unitPrice = item.getProduct().getBasePrice();

        if (item.getVariant() != null) {
            dto.setVariantId(item.getVariant().getId());
            dto.setSize(item.getVariant().getSize());
            dto.setColor(item.getVariant().getColor());

            // Giá thực tế = Giá gốc + Giá cộng thêm của Variant
            BigDecimal additionalPrice = item.getVariant().getAdditionalPrice() != null
                    ? item.getVariant().getAdditionalPrice()
                    : BigDecimal.ZERO;
            unitPrice = unitPrice.add(additionalPrice);
        }

        dto.setPrice(unitPrice); // Giá của 1 cái
        dto.setSubTotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()))); // Tổng giá của dòng này

        return dto;
    }

    // Hàm chuyển đổi cả cái Giỏ hàng (Cart) -> CartResponse
    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapCartItemToDto) // Gọi hàm map ở trên
                .collect(Collectors.toList());

        response.setItems(itemResponses);

        // --- TÍNH LẠI TỔNG TIỀN CẢ GIỎ HÀNG ---
        // Phải cộng tổng của các subTotal lại (vì mỗi subTotal đã chứa giá Variant rồi)
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalAmount(totalAmount);

        return response;
    }
}