package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.OrderDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/dispatch")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrderDispatchController {

    private final OrderDispatchService orderDispatchService;

    @PostMapping
    public ResponseEntity<BaseResponseDTO<String>> dispatchOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Gọi service để điều phối đơn hàng
        BaseResponseDTO<String> response = orderDispatchService.processOrders(username, false);
        return ResponseEntity.ok(response);
    }
}
