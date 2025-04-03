package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.auth.AuthRequestDTO;
import com.hungnguyen.srs_warehouse.dto.auth.AuthResponseDTO;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý xác thực người dùng (Login, Refresh Token)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponseDTO<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok( authService.authenticate(request));
    }
}