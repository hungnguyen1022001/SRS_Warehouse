package com.hungnguyen.srs_warehouse.dto.orderDetail;
import lombok.Builder;
@Builder
public record WarehouseDTO(
        String warehouseId,
        String name,
        String address
) {}
