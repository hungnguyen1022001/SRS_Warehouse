package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.service.OrderLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class OrderLabelController {
    private final OrderLabelService orderLabelService;

    @PostMapping("/export-labels")
    public ResponseEntity<byte[]> exportLabels(@RequestBody List<String> orderIds) {
        return orderLabelService.generateLabelExcel(orderIds);
    }
}
