package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.service.OrderService;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
/**
 * Controller xử lý các thao tác liên quan đến đơn hàng (Order)
 */
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
    public ResponseEntity<BaseResponseDTO<Map<String, Object>>> getOrderList(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        return ResponseEntity.ok(orderService.getOrderList(orderId,phone,status,warehouseId,page,limit));
    }

    @GetMapping("/detail/{orderId}")
    public ResponseEntity<BaseResponseDTO<OrderDetailDTO>> getOrderDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<String>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/ids")
    public ResponseEntity<BaseResponseDTO<List<String>>> getOrderIds(
            @RequestParam(required = false) String orderId) {
        return ResponseEntity.ok(orderService.getOrderIds(orderId));
    }
}
