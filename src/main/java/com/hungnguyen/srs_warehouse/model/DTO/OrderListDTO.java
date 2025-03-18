package com.hungnguyen.srs_warehouse.model.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
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
