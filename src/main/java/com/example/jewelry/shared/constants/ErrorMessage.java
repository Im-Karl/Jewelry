package com.example.jewelry.shared.constants;

public class ErrorMessage {
    // Auth & User
    public static final String USER_NOT_FOUND = "Không tìm thấy người dùng với thông tin cung cấp.";
    public static final String EMAIL_EXISTED = "Email này đã được đăng ký.";
    public static final String WRONG_PASSWORD = "Mật khẩu không chính xác.";
    public static final String BLANK_PASSWORD = "Mật khẩu không được để trống.";
    public static final String EMAIL_NOT_BLANK = "Email không được để trống.";
    public static final String EMAIL_INVALID = "Định dạng email không hợp lệ.";
    public static final String PASSWORD_MIN_LENGTH = "Mật khẩu phải có ít nhất 6 ký tự.";
    public static final String BLANK_NAME = "Họ tên không được để trống";
    public static final String USER_BANNED ="Tài khoản của bạn đang bị khoá hoặc bị cấm truy cập";
    // Product & Inventory
    public static final String PRODUCT_NOT_FOUND = "Sản phẩm không tồn tại.";
    public static final String CATEGORY_NOT_FOUND = "Danh mục không tồn tại.";
    public static final String OUT_OF_STOCK = "Sản phẩm %s không đủ số lượng tồn kho (chỉ còn %d).";
    public static final String STOCK_NOT_NEGATIVE = "Tồn kho không được là số âm.";

    // Order
    public static final String ORDER_NOT_FOUND = "Đơn hàng không tồn tại.";
    public static final String CART_EMPTY = "Giỏ hàng không được để trống.";
    public static final String ITEM_MIN_LENGTH = "Số lượng ít nhất là 1.";
    public static final String BLANK_ADDRESS = "Địa chỉ giao hàng là bắt buộc.";
    public static final String BLANK_PHONE = "Số điện thoại là bắt buộc.";


    // Gamification
    public static final String EMAIL_PLAYED = "Email này đã tham gia quay thưởng rồi!";
    public static final String PROMOTION_ENDED = "Chương trình quay thưởng đang bảo trì hoặc đã kết thúc.";

    // System
    public static final String INTERNAL_SERVER_ERROR = "Lỗi hệ thống, vui lòng thử lại sau.";
}