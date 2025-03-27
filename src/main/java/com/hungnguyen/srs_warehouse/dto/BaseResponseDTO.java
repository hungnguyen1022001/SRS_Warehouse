package com.hungnguyen.srs_warehouse.dto;

import com.hungnguyen.srs_warehouse.config.MessageConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseResponseDTO<T> {
    private int status;
    private String message;
    private T data;

    public static <T> BaseResponseDTO<T> success(String messageCode, T data) {
        return new BaseResponseDTO<>(1, MessageConfig.getMessage(messageCode), data);
    }

    public static <T> BaseResponseDTO<T> success(String messageCode) {
        return new BaseResponseDTO<>(1, MessageConfig.getMessage(messageCode), null);
    }

    public static <T> BaseResponseDTO<T> fail(String messageCode, T data) {
        return new BaseResponseDTO<>(0, MessageConfig.getMessage(messageCode), data);
    }

    public static <T> BaseResponseDTO<T> fail(String messageCode) {
        return new BaseResponseDTO<>(0, MessageConfig.getMessage(messageCode), null);
    }
}
