package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.model.DTO.OrderReportRequestDTO;
import com.hungnguyen.srs_warehouse.service.OrderReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderReportController {
    private final OrderReportService orderReportService;

    @PostMapping("/export-report")
    public ResponseEntity<BaseResponseDTO<byte[]>> exportReport(@RequestBody OrderReportRequestDTO request) throws IOException {
        BaseResponseDTO<byte[]> response = orderReportService.generateReport(
                request.getWarehouseIds(),
                request.getStartDate(),
                request.getEndDate(),
                request.isMonthly()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + orderReportService.generateFileName())
                .body(response);
    }
}
