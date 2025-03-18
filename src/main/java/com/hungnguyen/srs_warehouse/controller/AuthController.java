package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.AuthRequestDTO;
import com.hungnguyen.srs_warehouse.model.DTO.AuthResponseDTO;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> tokenRequest) {
        String refreshToken = tokenRequest.get("refreshToken");

        if (jwtUtils.validateToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            String warehouseId = jwtUtils.getWarehouseIdFromToken(refreshToken);

            String newAccessToken = jwtUtils.generateToken(username, warehouseId, false);
            return ResponseEntity.ok(Map.of(
                    "status", 1,
                    "message", "Token refreshed successfully",
                    "accessToken", newAccessToken,
                    "refreshToken", refreshToken
            ));
        }

        return ResponseEntity.status(403).body(Map.of(
                "status", 0,
                "message", "AUTH_002"
        ));
    }
}
