package com.hungnguyen.srs_warehouse.dto.orderDetail;
import java.time.LocalDateTime;
public record OrderLabelDTO(
        String orderId,
        SupplierDTO supplier,
        ReceiverDTO receiver,
        WarehouseDTO warehouse,
        LocalDateTime createdAt,
        String status
) {}
