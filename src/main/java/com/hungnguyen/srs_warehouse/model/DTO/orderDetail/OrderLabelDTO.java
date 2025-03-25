package com.hungnguyen.srs_warehouse.model.DTO.orderDetail;
import java.time.LocalDateTime;
public record OrderLabelDTO(
        String orderId,
        SupplierDTO supplier,
        ReceiverDTO receiver,
        WarehouseDTO warehouse,
        LocalDateTime createdAt,
        String status
) {}
