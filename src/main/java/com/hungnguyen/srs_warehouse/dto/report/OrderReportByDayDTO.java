package com.hungnguyen.srs_warehouse.dto.report;

import java.time.LocalDateTime;

public class OrderReportByDayDTO {
    private String warehouseId;
    private String warehouseName;
    private LocalDateTime storedAt;
    private Long orderCount;

    // Constructor matching the query in OrderRepository
    public OrderReportByDayDTO(String warehouseId, String warehouseName, LocalDateTime storedAt, Long orderCount) {
        this.warehouseId = warehouseId != null ? warehouseId : "";
        this.warehouseName = warehouseName != null ? warehouseName : "Unknown";
        this.storedAt = storedAt;
        this.orderCount = orderCount != null ? orderCount : 0L;
    }

    // Getters and setters
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public LocalDateTime getStoredAt() { return storedAt; }
    public void setStoredAt(LocalDateTime storedAt) { this.storedAt = storedAt; }

    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
}
