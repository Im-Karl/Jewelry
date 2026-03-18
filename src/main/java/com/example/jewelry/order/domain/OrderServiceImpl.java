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
import com.example.jewelry.shared.enums.CustomerTier;
import com.example.jewelry.shared.enums.OrderStatus;
import com.example.jewelry.shared.exception.DomainException;
import com.example.jewelry.shared.exception.DomainExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private int calculateTierDiscountPercent(UUID userId) {
        // Lấy tất cả các đơn hàng ĐÃ HOÀN THÀNH của User này
        BigDecimal totalSpent = orderRepository.findByUserId(userId).stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double spent = totalSpent.doubleValue();

        if (spent >= 5_000_000_000L) return 15;      // Royal (>= 5 tỷ)
        if (spent >= 2_000_000_000L) return 10;      // Elite (2 tỷ - 5 tỷ)
        if (spent >= 800_000_000L) return 8;         // Diamond (800tr - 2 tỷ)
        if (spent >= 200_000_000L) return 5;         // Gold (200tr - 800tr)
        if (spent >= 50_000_000L) return 3;          // Silver (50tr - 200tr)
        return 0;                                    // Member (< 50tr)
    }

    @Override
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.CART_EMPTY));

        if (cart.getItems().isEmpty()) {
            throw new DomainException(DomainExceptionCode.CART_EMPTY);
        }

        List<CartItem> selectedItems = cart.getItems().stream()
                .filter(item -> request.getCartItemIds().contains(item.getId()))
                .collect(Collectors.toList());

        if (selectedItems.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sản phẩm nào được chọn trong giỏ hàng!");
        }

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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : selectedItems) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();
            int quantity = cartItem.getQuantity();

            int rowsUpdated = variantRepository.deductStock(variant.getId(), quantity);
            if (rowsUpdated == 0) {
                throw new DomainException(DomainExceptionCode.OUT_OF_STOCK);
            }

            BigDecimal unitPrice = product.getBasePrice().add(variant.getAdditionalPrice());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotal);

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

        int tierPercent = cart.getUser().getTier() != null ? cart.getUser().getTier().getDiscountPercent() : 0;
        BigDecimal tierDiscountAmount = totalAmount
                .multiply(BigDecimal.valueOf(tierPercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        String couponCode = request.getCouponCode();

        if( couponCode != null && !couponCode.trim().isEmpty() ) {
            Optional<UserCoupon> userCoupon = userCouponRepository.findByGeneratedCode(couponCode);

            if(userCoupon.isPresent()) {
                UserCoupon uCoupon = userCoupon.get();
                if(!uCoupon.getEmail().equals(cart.getUser().getEmail())) throw new RuntimeException("Mã giảm giá không thuộc về bạn");
                if(uCoupon.isUsed()) throw new RuntimeException("Mã giảm giá đã được sử dụng");
                if(uCoupon.getExpiredAt().isBefore(LocalDateTime.now())) throw new RuntimeException("Mã giảm giá đã hết hạn");

                couponDiscountAmount = uCoupon.getDiscountAmount(); // Dùng số tiền cố định
                uCoupon.setUsed(true);
                userCouponRepository.save(uCoupon);
            } else {
                Optional<PublicCoupon> publicCouponOpt = publicCouponRepository.findByCodeAndActiveTrue(couponCode.toUpperCase());
                if(publicCouponOpt.isPresent()){
                    PublicCoupon pCoupon = publicCouponOpt.get();
                    LocalDateTime now = LocalDateTime.now();
                    if (pCoupon.getExpiredAt() != null && pCoupon.getExpiredAt().isBefore(now)) throw new RuntimeException("Mã giảm giá đã hết hạn!");
                    if (pCoupon.getStartAt() != null && pCoupon.getStartAt().isAfter(now)) throw new RuntimeException("Mã chưa đến đợt áp dụng!");
                    if (pCoupon.getMaxUsage() > 0 && pCoupon.getUsedCount() >= pCoupon.getMaxUsage()) throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");

                    couponDiscountAmount = pCoupon.getDiscountAmount(); // Dùng số tiền cố định
                    pCoupon.setUsedCount(pCoupon.getUsedCount() + 1);
                    publicCouponRepository.save(pCoupon);
                } else {
                    throw new RuntimeException("Mã giảm giá không tồn tại hoặc không hợp lệ!");
                }
            }
        }

        // TỔNG TIỀN ĐƯỢC GIẢM = GIẢM HẠNG THÀNH VIÊN + GIẢM VOUCHER
        BigDecimal totalDiscountAmount = tierDiscountAmount.add(couponDiscountAmount);

        BigDecimal tempAmount = totalAmount.subtract(totalDiscountAmount);
        if (tempAmount.compareTo(BigDecimal.ZERO) < 0) tempAmount = BigDecimal.ZERO;

        // ==========================================
        // 3. TÍNH ĐIỂM THƯỞNG (Points)
        // ==========================================
        int pointsUsed = 0;
        BigDecimal pointsDiscount = BigDecimal.ZERO;

        if (request.getPointsToUse() > 0 && tempAmount.compareTo(BigDecimal.ZERO) > 0) {
            User user = cart.getUser();
            int userCurrentPoints = user.getLoyaltyPoints();
            int requestedPoints = request.getPointsToUse();

            if (requestedPoints > userCurrentPoints) {
                throw new RuntimeException("Không đủ điểm thưởng (" + userCurrentPoints + " hiện có).");
            }

            BigDecimal requestedValue = BigDecimal.valueOf(requestedPoints);

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
        order.setDiscountAmount(totalDiscountAmount); // Ghi nhận TỔNG SỐ TIỀN ĐƯỢC GIẢM vào DB
        order.setPointsUsed(pointsUsed);
        order.setPointsDiscountAmount(pointsDiscount);
        order.setFinalAmount(finalAmount);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().removeAll(selectedItems);
        cartRepository.save(cart);

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
        dto.setDeliveredAt(order.getDeliveredAt());
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
    @Transactional
    public void cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ORDER_NOT_FOUND));

        if(!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền huỷ đơn hàng này!");
        }

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể huỷ đơn hàng ở trạng thái Pending");
        }

        order.setStatus(OrderStatus.CANCELLED);

        for(OrderItem item : order.getItems()) {
            if (item.getVariantId() != null) {
                ProductVariant variant = variantRepository.findById(item.getVariantId()).orElse(null);
                if(variant != null) {
                    variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                    variantRepository.save(variant);
                }
            }
        }
        if(order.getPointsUsed() > 0) {
            User user = order.getUser();
            user.setLoyaltyPoints(user.getLoyaltyPoints() + order.getPointsUsed());
            userRepository.save(user);
        }

        if(order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            String code = order.getCouponCode();

            Optional<UserCoupon> userCouponOpt = userCouponRepository.findByGeneratedCode(code);
            if(userCouponOpt.isPresent()) {
                UserCoupon userCoupon = userCouponOpt.get();
                userCoupon.setUsed(false);
                userCouponRepository.save(userCoupon);
            }else{
                Optional<PublicCoupon> publicCouponOpt = publicCouponRepository.findByCode(code.toUpperCase());
                if(publicCouponOpt.isPresent()) {
                    PublicCoupon publicCoupon = publicCouponOpt.get();
                    if(publicCoupon.getUsedCount() > 0){
                        publicCoupon.setUsedCount(publicCoupon.getUsedCount() - 1);
                        publicCouponRepository.save(publicCoupon);
                    }
                }
            }

            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(DomainExceptionCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Đơn hàng đã hủy, không thể cập nhật trạng thái khác!");
        }

        if (newStatus == OrderStatus.COMPLETED && order.getStatus() != OrderStatus.COMPLETED) {
            order.setDeliveredAt(LocalDateTime.now());

            User user = order.getUser();
            BigDecimal newTotalSpent = user.getTotalSpent().add(order.getFinalAmount());
            user.setTotalSpent(newTotalSpent);

            upgradeTierIfNeeded(user, newTotalSpent);

            userRepository.save(user);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return mapToDto(order);
    }

    private void upgradeTierIfNeeded(User user, BigDecimal totalSpent) {
        double spent = totalSpent.doubleValue();
        CustomerTier newTier = CustomerTier.MEMBER;

        if (spent >= 5_000_000_000L) newTier = CustomerTier.ROYAL;
        else if (spent >= 2_000_000_000L) newTier = CustomerTier.ELITE;
        else if (spent >= 800_000_000L) newTier = CustomerTier.DIAMOND;
        else if (spent >= 200_000_000L) newTier = CustomerTier.GOLD;
        else if (spent >= 50_000_000L) newTier = CustomerTier.SILVER;

        user.setTier(newTier);
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