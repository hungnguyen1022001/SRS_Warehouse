package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.OrderLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderLabelController {
    private final OrderLabelService orderLabelService;

    @PostMapping("/export-labels")
    public ResponseEntity<BaseResponseDTO<String>> exportLabels(@RequestBody List<String> orderIds) {
        BaseResponseDTO<String> response = orderLabelService.generateLabelExcel(orderIds);
        return ResponseEntity.ok(response);
    }
}
