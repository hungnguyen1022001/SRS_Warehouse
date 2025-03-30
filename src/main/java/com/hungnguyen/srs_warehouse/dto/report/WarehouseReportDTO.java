package com.hungnguyen.srs_warehouse.dto.report;

import java.time.LocalDate;
import java.time.YearMonth;

public record WarehouseReportDTO(
        String warehouseId,
        String warehouseName,
        LocalDate reportDate,
        YearMonth reportMonth,
        int totalOrders
) {
    /**
     * Trả về ngày báo cáo nếu có, hoặc null nếu đây là báo cáo theo tháng.
     */
    public String formatReportDate() {
        return (reportDate != null) ? reportDate.toString() : null;
    }

    /**
     * Trả về tháng báo cáo nếu có, hoặc null nếu đây là báo cáo theo ngày.
     */
    public String formatReportMonth() {
        return (reportMonth != null) ? reportMonth.toString() : null;
    }
}
