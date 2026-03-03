package com.example.jewelry.notification.domain;

import com.example.jewelry.order.domain.Order;
import com.example.jewelry.order.domain.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail()); // Gửi cho NGƯỜI MUA -> Phải thấy giá
            helper.setSubject("Xác nhận đơn hàng Silveré - #" + order.getId());

            String htmlContent = buildOrderEmailContent(order);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Order confirmation email sent to {}", order.getUser().getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        }
    }

    private String buildOrderEmailContent(Order order) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        StringBuilder itemsHtml = new StringBuilder();
        itemsHtml.append("<table style='width: 100%; border-collapse: collapse;'>");
        itemsHtml.append("<tr style='background-color: #f2f2f2;'>")
                .append("<th style='padding: 8px; border: 1px solid #ddd;'>Sản phẩm</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd;'>SL</th>")
                .append("<th style='padding: 8px; border: 1px solid #ddd;'>Đơn giá</th>"); // Luôn hiện cột giá
        itemsHtml.append("</tr>");

        for (OrderItem item : order.getItems()) {
            itemsHtml.append("<tr>");
            itemsHtml.append("<td style='padding: 8px; border: 1px solid #ddd;'>")
                    .append(item.getProduct().getName())
                    .append("</td>");
            itemsHtml.append("<td style='padding: 8px; border: 1px solid #ddd; text-align: center;'>")
                    .append(item.getQuantity())
                    .append("</td>");
            itemsHtml.append("<td style='padding: 8px; border: 1px solid #ddd; text-align: right;'>")
                    .append(currencyFormatter.format(item.getPriceAtPurchase())) // Luôn hiện giá tiền
                    .append("</td>");
            itemsHtml.append("</tr>");
        }
        itemsHtml.append("</table>");

        // Phần tổng tiền (Luôn hiển thị vì đây là biên lai cho người mua)
        String totalSection = String.format("<h3 style='text-align: right; color: #d9534f;'>Tổng thanh toán: %s</h3>",
                currencyFormatter.format(order.getTotalAmount()));

        // Phần Gifting Mode: Chỉ hiển thị thêm lời chúc để người mua confirm
        String giftSection = "";
        if (order.isGift()) {
            giftSection = String.format(
                    "<div style='background-color: #e3f2fd; padding: 15px; border: 1px solid #90caf9; margin-top: 20px; border-radius: 8px;'>" +
                            "   <h4 style='color: #0d47a1; margin-top: 0;'>Ghi chú quà tặng:</h4>" +
                            "   <p>Đơn hàng này sẽ được gói quà và <b>ẩn giá trên phiếu giao hàng</b> gửi tới người nhận.</p>" +
                            "   <p style='font-style: italic;'>Lời nhắn: \"%s\"</p>" +
                            "</div>",
                    order.getGiftMessage() != null ? order.getGiftMessage() : "Gửi tặng những điều tốt đẹp nhất!"
            );
        }

        return """
            <html>
            <body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>
                <div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>
                    <h2 style='color: #0056b3; text-align: center;'>Cảm ơn bạn đã đặt hàng tại Silveré!</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Đơn hàng <strong>#%s</strong> của bạn đã được xác nhận thành công.</p>
                    
                    <div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>
                        <p style='margin: 5px 0;'><strong>Người nhận:</strong> %s</p>
                        <p style='margin: 5px 0;'><strong>SĐT:</strong> %s</p>
                        <p style='margin: 5px 0;'><strong>Địa chỉ giao hàng:</strong> %s</p>
                    </div>

                    %s %s %s <p style='margin-top: 30px; font-size: 12px; color: #777; text-align: center;'>
                        Đây là email xác nhận tự động. Vui lòng không trả lời email này.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                order.getUser().getFullName(),
                order.getId(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getShippingAddress(),
                itemsHtml.toString(),
                totalSection,
                giftSection
        );
    }

    @Async
    public void sendCouponEmail(String toEmail, String couponCode, int discount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bạn đã trúng thưởng từ Silveré!");

            String htmlContent = """
                <div style='text-align: center; font-family: Arial;'>
                    <h2 style='color: #d9534f;'>Chúc mừng bạn!</h2>
                    <p>Bạn đã quay trúng voucher giảm giá <strong>%d%%</strong>.</p>
                    <div style='border: 2px dashed #333; padding: 10px; margin: 20px auto; width: 200px; font-size: 20px; font-weight: bold;'>
                        %s
                    </div>
                    <p>Sử dụng mã này khi thanh toán nhé!</p>
                </div>
            """.formatted(discount, couponCode);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send coupon email", e);
        }
    }
}