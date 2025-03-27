package com.hungnguyen.srs_warehouse.dto.orderDetail;

import java.sql.Timestamp;

public record OrderHistoryDTO(
        Timestamp performedAt,
        int status,
        String failureReason
) {}

