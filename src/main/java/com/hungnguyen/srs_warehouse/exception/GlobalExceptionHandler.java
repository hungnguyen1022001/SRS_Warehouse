package com.hungnguyen.srs_warehouse.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    // Xử lý lỗi chung (Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", getMessage("SERVER_ERROR"));  // Lấy từ messages.properties
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Xử lý lỗi Unauthorized
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(SecurityException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", getMessage("UNAUTHORIZED"));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // Xử lý lỗi tùy chỉnh (CustomException)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 0);
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
