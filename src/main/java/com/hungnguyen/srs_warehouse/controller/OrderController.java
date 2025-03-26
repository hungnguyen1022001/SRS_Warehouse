package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.service.OrderService;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.model.DTO.orderDetail.OrderDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getOrderList(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) List<String> warehouseIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        OrderSearchCriteria criteria = new OrderSearchCriteria();
        criteria.setOrderId(orderId);
        criteria.setPhone(phone);
        criteria.setStatus(status);
        criteria.setWarehouseIds(warehouseIds != null ? warehouseIds : List.of());
        criteria.setPage(page);
        criteria.setSize(size);

        Map<String, Object> response = orderService.getOrderList(criteria);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<BaseResponseDTO<OrderDetailDTO>> getOrderDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<String>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(new BaseResponseDTO<>(0, "UNAUTHORIZED", null));
        }

        String token = authHeader.substring(7);
        return ResponseEntity.ok(orderService.createOrder(request, token));
    }

    @GetMapping("/ids")
    public ResponseEntity<BaseResponseDTO<List<String>>> getOrderIds(
            @RequestParam(required = false) String orderId) {
        return ResponseEntity.ok(orderService.getOrderIds(orderId));
    }
}