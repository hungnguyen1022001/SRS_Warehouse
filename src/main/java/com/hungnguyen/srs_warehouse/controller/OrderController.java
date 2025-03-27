package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.dto.orderReport.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.service.OrderService;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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

    // ✅ Giữ nguyên phương thức getOrderList() không thay đổi
    @GetMapping("/list")
    public ResponseEntity<BaseResponseDTO<OrderListResponse>> getOrderList(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) List<String> warehouseIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        OrderSearchCriteria criteria = new OrderSearchCriteria(orderId, phone, status, warehouseIds, page, size);
        BaseResponseDTO<OrderListResponse> response = orderService.getOrderList(criteria);

        return ResponseEntity.status(response.getStatus() == 1 ? 200 : 400).body(response);
    }


    // ✅ Lấy chi tiết đơn hàng với BaseResponseDTO<OrderDetailDTO>
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<BaseResponseDTO<OrderDetailDTO>> getOrderDetail(@PathVariable String orderId) {
        BaseResponseDTO<OrderDetailDTO> response = orderService.getOrderDetail(orderId);
        return ResponseEntity.status(response.getStatus() == 1 ? 200 : 400).body(response);
    }

    // ✅ Tạo đơn hàng với BaseResponseDTO<String>
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<String>> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401)
                    .body(BaseResponseDTO.fail("UNAUTHORIZED"));
        }

        String token = authHeader.substring(7);
        BaseResponseDTO<String> response = orderService.createOrder(request, token);
        return ResponseEntity.status(response.getStatus() == 1 ? 200 : 400).body(response);
    }

    @GetMapping("/ids")
    public ResponseEntity<BaseResponseDTO<List<String>>> getOrderIds(
            @RequestParam(required = false) String orderId) {

        BaseResponseDTO<List<String>> response = orderService.getOrderIds(orderId);
        return ResponseEntity.status(response.getStatus() == 1 ? 200 : 400).body(response);
    }
}
