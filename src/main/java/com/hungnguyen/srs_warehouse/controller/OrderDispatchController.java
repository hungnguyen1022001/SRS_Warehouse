package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.service.OrderDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/dispatch")
@RequiredArgsConstructor
public class OrderDispatchController {

    private final OrderDispatchService orderDispatchService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<BaseResponseDTO<String>> dispatchOrders(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponseDTO<>(0, "Token khong hop le!", null));
        }

        // Lấy username từ JWT
        String jwtToken = token.replace("Bearer ", "");
        String username = jwtUtils.getUsernameFromToken(jwtToken);

        if (username == null || username.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponseDTO<>(0, "Khong lay duoc thong tin nguoi dung tu token!", null));
        }

        // Gọi service để điều phối đơn hàng (API gọi, không phải batch job)
        BaseResponseDTO<String> response = orderDispatchService.processOrders(username, false);
        return ResponseEntity.ok(response);
    }
}
