package com.example.jewelry.order.domain;

import com.example.jewelry.auth.domain.User;
import com.example.jewelry.auth.domain.UserRepository;
import com.example.jewelry.cart.domain.Cart;
import com.example.jewelry.cart.domain.CartItem;
import com.example.jewelry.cart.domain.CartRepository; // Dùng trực tiếp Repo để lấy Entity
import com.example.jewelry.notification.domain.EmailService;
import com.example.jewelry.order.dto.CreateOrderRequest;
import com.example.jewelry.order.dto.OrderItemResponse;
import com.example.jewelry.gamification.domain.*;
import com.example.jewelry.order.dto.OrderResponse;
import com.example.jewelry.order.web.OrderService;
import com.example.jewelry.product.domain.Product;
import com.example.jewelry.product.domain.ProductRepository;
import com.example.jewelry.product.domain.ProductVariant;
import com.example.jewelry.product.domain.ProductVariantRepository;
import com.example.jewelry.product.web.ProductService;
import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final EmailService emailService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserCouponRepository userCouponRepository;
    private final PublicCouponRepository publicCouponRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        // 1. Lấy Cart Entity của User
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CART_EMPTY));

        if (cart.getItems().isEmpty()) {
            throw new DomainException(DomainExceptionCode.CART_EMPTY);
        }

        // 2. Lọc ra các CartItem mà user muốn mua (dựa trên List ID gửi lên)
        // Logic: Chỉ lấy những item nào có ID nằm trong request.getCartItemIds()
        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> request.getCartItemIds().contains(item.getId()))
                .collect(Collectors.toList());

        // Validate: Nếu user gửi ID tào lao không có trong giỏ, hoặc list rỗng
        if (selectedItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm nào được chọn trong giỏ hàng!");
        }

        // (Optional) Kiểm tra xem user có gửi thiếu ID nào không tồn tại không
//        if (selectedItems.size() != request.getCartItemIds().size()) {
//            // Có thể warning hoặc throw lỗi tùy nghiệp vụ
//        }

        // 3. Tạo Order Header
        Order order = Order.builder()
                .user(cart.getUser())
                .shippingAddress(request.getShippingAddress())
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .paymentMethod(request.getPaymentMethod())
                .gift(request.isGift())
                .giftMessage(request.getGiftMessage())
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : selectedItems) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();
            int quantity = cartItem.getQuantity();

            // A. Trừ tồn kho (Atomic Update)
            int rowsUpdated = variantRepository.deductStock(variant.getId(), quantity);
            if (rowsUpdated == 0) {
                throw new DomainException(DomainExceptionCode.OUT_OF_STOCK); // Nhớ thêm enum này
            }

            // B. Tính tiền
            BigDecimal unitPrice = product.getBasePrice().add(variant.getAdditionalPrice());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

            // C. Tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variantId(variant.getId())
                    .size(variant.getSize())
                    .color(variant.getColor())
                    .quantity(quantity)
                    .priceAtPurchase(unitPrice)
                    .build();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = request.getCouponCode();

        if( couponCode != null && !couponCode.trim().isEmpty() ) {
            Optional<UserCoupon> userCoupon = userCouponRepository.findByGeneratedCode((couponCode));

            if(userCoupon.isPresent()) {
                UserCoupon uCoupon = userCoupon.get();

                if(!uCoupon.getEmail().equals(cart.getUser().getEmail())) {
                    throw new RuntimeException("Mã giảm giá không thuộc về tài khoản của bạn");
                }
                if(uCoupon.isUsed()){
                    throw new RuntimeException("Mã giảm giá đã được sử dụng");
                }
                if(uCoupon.getExpiredAt().isBefore(LocalDateTime.now())){
                    throw new RuntimeException("Mã giảm giá đã hết hạn");
                }

                discountAmount = calculateDiscount(totalAmount, uCoupon.getDiscountPercent());

                uCoupon.setUsed(true);
                userCouponRepository.save(uCoupon);
            }else {
                Optional<PublicCoupon> publicCouponOpt = publicCouponRepository.findByCodeAndActiveTrue(couponCode.toUpperCase());

                if(publicCouponOpt.isPresent()){
                    PublicCoupon pCoupon = publicCouponOpt.get();

                    LocalDateTime now = LocalDateTime.now();
                    if (pCoupon.getExpiredAt() != null && pCoupon.getExpiredAt().isBefore(now)) {
                        throw new RuntimeException("Mã giảm giá đã hết hạn!");
                    }
                    if (pCoupon.getStartAt() != null && pCoupon.getStartAt().isAfter(now)) {
                        throw new RuntimeException("Mã giảm giá chưa đến đợt áp dụng!");
                    }
                    // Validate Usage Limit (Nếu maxUsage != -1)
                    if (pCoupon.getMaxUsage() > 0 && pCoupon.getUsedCount() >= pCoupon.getMaxUsage()) {
                        throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
                    }

                    discountAmount = calculateDiscount(totalAmount, pCoupon.getDiscountPercent());

                    pCoupon.setUsedCount(pCoupon.getUsedCount() + 1);
                    publicCouponRepository.save(pCoupon);
                }else{
                    throw new RuntimeException("Mã giảm giá không tồn tại hoặc không hợp lệ!");
                }
            }
        }

        BigDecimal tempAmount = totalAmount.subtract(discountAmount);
        if (tempAmount.compareTo(BigDecimal.ZERO) < 0) tempAmount = BigDecimal.ZERO;

        int pointsUsed = 0;
        BigDecimal pointsDiscount = BigDecimal.ZERO;

        if (request.getPointsToUse() > 0 && tempAmount.compareTo(BigDecimal.ZERO) > 0) {
            User user = cart.getUser();
            int userCurrentPoints = user.getLoyaltyPoints();
            int requestedPoints = request.getPointsToUse();

            if (requestedPoints > userCurrentPoints) {
                throw new RuntimeException("Bạn không đủ điểm thưởng để sử dụng (" + userCurrentPoints + " hiện có).");
            }

            BigDecimal requestedValue = BigDecimal.valueOf(requestedPoints); // 1 điểm = 1 VND

            if (requestedValue.compareTo(tempAmount) > 0) {
                pointsDiscount = tempAmount;
                pointsUsed = tempAmount.intValue();
            } else {
                pointsDiscount = requestedValue;
                pointsUsed = requestedPoints;
            }

            user.setLoyaltyPoints(userCurrentPoints - pointsUsed);
            userRepository.save(user);
        }

        BigDecimal finalAmount = tempAmount.subtract(pointsDiscount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        order.setCouponCode(couponCode);
        order.setDiscountAmount(discountAmount);
        order.setPointsUsed(pointsUsed);
        order.setPointsDiscountAmount(pointsDiscount);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().removeAll(selectedItems);
        cartRepository.save(cart); // Cập nhật lại giỏ hàng

        emailService.sendOrderConfirmation(savedOrder);
        return mapToDto(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private BigDecimal calculateDiscount(BigDecimal total, int percent) {
        return total.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));
    }

    private OrderResponse mapToDto(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setGift(order.isGift());
        dto.setGiftMessage(order.getGiftMessage());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setCouponCode(order.getCouponCode());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalAmount(order.getFinalAmount() != null ? order.getFinalAmount() : order.getTotalAmount());

        if (order.getUser() != null) {
            dto.setUserName(order.getUser().getFullName());
            dto.setUserEmail(order.getUser().getEmail());
        }

        List<OrderItemResponse> itemDtos = order.getItems().stream().map(item -> {
            OrderItemResponse itemDto = new OrderItemResponse();
            itemDto.setId(item.getId());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            itemDto.setVariantId(item.getVariantId());
            itemDto.setSize(item.getSize());
            itemDto.setColor(item.getColor());

            if (item.getProduct() != null) {
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setProductMainImageUrl(item.getProduct().getMainImageUrl());
            }
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        return dto;
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ORDER_NOT_FOUND));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteMyOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xóa đơn hàng này");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xóa đơn hàng khi đang chờ xử lý");
        }
        orderRepository.delete(order);
    }
}