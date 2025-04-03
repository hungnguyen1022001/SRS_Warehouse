package com.hungnguyen.srs_warehouse.exception;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Xử lý lỗi chung (Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponseDTO.fail("SERVER_ERROR"));
    }

    // Xử lý lỗi Unauthorized (UnauthorizedAccessException)
    @ExceptionHandler(CustomExceptions.UnauthorizedAccessException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleUnauthorizedException(CustomExceptions.UnauthorizedAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponseDTO.fail(ex.getMessageCode()));
    }

    // Xử lý lỗi Not Found (NotFoundException)
    @ExceptionHandler(CustomExceptions.NotFoundException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleNotFoundException(CustomExceptions.NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponseDTO.fail(ex.getMessageCode()));
    }

    @ExceptionHandler(CustomExceptions.CustomException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleCustomException(CustomExceptions.CustomException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponseDTO.fail(ex.getMessageCode()));
    }

    @ExceptionHandler(CustomExceptions.FileGenerationException.class)
    public ResponseEntity<BaseResponseDTO<Void>> handleFileGenerationException(CustomExceptions.FileGenerationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponseDTO.fail(ex.getMessageCode()));
    }



}
