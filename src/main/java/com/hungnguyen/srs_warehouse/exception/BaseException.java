package com.hungnguyen.srs_warehouse.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String messageCode;

    public BaseException(String messageCode) {
        this.messageCode = messageCode;
    }
}
