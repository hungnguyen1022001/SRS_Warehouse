package com.hungnguyen.srs_warehouse.dto.orderList;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
public class OrderListDTO {
    private String orderId;
    private LocalDateTime createdAt;
    private Integer status;

    // Warehouse Info
    private String warehouseId;
    private String warehouseName;

    // Supplier Info
    private String supplierName;
    private String supplierAddress;
    private String supplierPhone;
    private String supplierEmail;

    // Receiver Info
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private String receiverEmail;

    // Order Dates
    private LocalDateTime storedAt;
    private LocalDateTime deliveredAt;
    private Integer failedDeliveries;
    private LocalDateTime returnAt;
}
