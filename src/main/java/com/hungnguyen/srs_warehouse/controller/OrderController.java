package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
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

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequest request,
                                                           @RequestHeader("Authorization") String authHeader) {
        // ✅ Kiểm tra token có đúng định dạng "Bearer token"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("status", 0, "message", "Unauthorized - Missing Bearer Token"));
        }

        // ✅ Lấy token từ header (loại bỏ "Bearer ")
        String token = authHeader.substring(7);
        Map<String, Object> response = orderService.createOrder(request, token);

        return ResponseEntity.ok(response);
    }


}
