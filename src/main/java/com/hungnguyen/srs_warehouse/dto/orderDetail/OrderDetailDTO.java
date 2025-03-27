package com.hungnguyen.srs_warehouse.dto.orderDetail;

import java.sql.Timestamp;
import java.util.List;

public record OrderDetailDTO(
        String orderId,
        Timestamp createdAt,
        Timestamp updatedAt,
        String createdBy,
        int status,
        Timestamp storedAt,
        Timestamp deliveredAt,
        Integer failedDeliveries,
        Timestamp returnAt,
        WarehouseDTO warehouse,
        SupplierDTO supplier,
        ReceiverDTO receiver,
        List<OrderHistoryDTO> orderHistories
) {}
