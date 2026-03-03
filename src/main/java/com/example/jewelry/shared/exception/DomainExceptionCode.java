package com.example.jewelry.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum DomainExceptionCode {
    /* USER & AUTH */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED),
    USER_EXISTED(HttpStatus.CONFLICT),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    USER_BANNED(HttpStatus.FORBIDDEN),


    /* PRODUCT */
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND),
//    INSUFFICIENT_STOCK(HttpStatus.CONFLICT),
    OUT_OF_STOCK(HttpStatus.CONFLICT),


    /* CODE */
    COUPON_CODE_EXISTED(HttpStatus.CONFLICT),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND),

    /* ORDER */
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND),
    ORDER_CANNOT_BE_DELETED(HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND),
    CART_EMPTY(HttpStatus.NOT_FOUND);

    private final HttpStatus httpStatus;

    DomainExceptionCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}