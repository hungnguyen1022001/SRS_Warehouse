package com.hungnguyen.srs_warehouse.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderReportService {
    ResponseEntity<byte[]> generateDailyOrderReport(List<String> warehouseIds, String fromDate, String toDate);
    ResponseEntity<byte[]> generateMonthlyOrderReport(List<String> warehouseIds, String fromDate, String toDate);
}
