package com.example.jewelry.cart.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.cart.dto.AddToCartRequest;
import com.example.jewelry.cart.dto.CartItemResponse; // Import
import com.example.jewelry.cart.dto.CartResponse;     // Import
import com.example.jewelry.cart.web.CartService;
import com.example.jewelry.product.domain.Product;
import com.example.jewelry.product.domain.ProductRepository;
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

    @Override
    public CartResponse getMyCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapToCartResponse(cart); // <--- Map sang DTO
    }

    @Override
    @Transactional
    public CartResponse addToCart(UUID userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_NOT_FOUND));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart); // <--- Map sang DTO
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(UUID userId, UUID cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CART_EMPTY)); // Hoặc lỗi khác tùy bạn

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

    // HÀM QUAN TRỌNG NHẤT: CHUYỂN ENTITY SANG DTO
    private CartResponse mapToCartResponse(Cart cart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            BigDecimal price = item.getProduct().getBasePrice();
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(subTotal);

            itemResponses.add(CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productImage(item.getProduct().getMainImageUrl())
                    .price(price)
                    .quantity(item.getQuantity())
                    .subTotal(subTotal)
                    .build());
        }

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }
}