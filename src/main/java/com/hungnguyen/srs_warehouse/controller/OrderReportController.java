package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.service.OrderReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class OrderReportController {
    private final OrderReportService orderReportService;

    @GetMapping("/export/daily")
    public ResponseEntity<byte[]> exportDailyOrderReport(
            @RequestParam List<String> warehouseIds,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        return orderReportService.generateDailyOrderReport(warehouseIds, fromDate, toDate);
    }

    @GetMapping("/export/monthly")
    public ResponseEntity<byte[]> exportMonthlyOrderReport(
            @RequestParam List<String> warehouseIds,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        return orderReportService.generateMonthlyOrderReport(warehouseIds, fromDate, toDate);
    }
}

