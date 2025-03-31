package com.hungnguyen.srs_warehouse.dto.report;

import java.time.YearMonth;

public class OrderReportByMonthDTO {
    private String warehouseId;
    private String warehouseName;
    private YearMonth reportMonth;
    private Long orderCount;

    public OrderReportByMonthDTO(String warehouseId, String warehouseName, Integer year, Integer month, Long orderCount) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.reportMonth = YearMonth.of(year, month);
        this.orderCount = orderCount;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public YearMonth getReportMonth() {
        return reportMonth;
    }

    public Long getOrderCount() {
        return orderCount;
    }
}
