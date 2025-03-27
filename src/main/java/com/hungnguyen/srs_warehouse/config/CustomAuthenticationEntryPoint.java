package com.hungnguyen.srs_warehouse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Locale;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;

    public CustomAuthenticationEntryPoint(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401

        // Lấy mã lỗi từ messages.properties
        String message = messageSource.getMessage("UNAUTHORIZED", null, "SYSS-3000 : Unauthorized", Locale.getDefault());

        BaseResponseDTO<String> errorResponse = new BaseResponseDTO<>(0, message, null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

