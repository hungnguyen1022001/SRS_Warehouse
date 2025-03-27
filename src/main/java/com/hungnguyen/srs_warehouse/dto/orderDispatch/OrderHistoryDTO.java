package com.hungnguyen.srs_warehouse.dto.orderDispatch;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderHistoryDTO {
    private String historyId;
    private String performedBy;
    private LocalDateTime performedAt;
    private String orderId;
    private String warehouseId;
    private int status;
    private String failureReason;

    // Constructor mặc định
    public OrderHistoryDTO() {}

    // Constructor đầy đủ
    public OrderHistoryDTO(String historyId, String performedBy, LocalDateTime performedAt,
                           String orderId, String warehouseId, int status, String failureReason) {
        this.historyId = historyId;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
        this.orderId = orderId;
        this.warehouseId = warehouseId;
        this.status = status;
        this.failureReason = failureReason;
    }
}
