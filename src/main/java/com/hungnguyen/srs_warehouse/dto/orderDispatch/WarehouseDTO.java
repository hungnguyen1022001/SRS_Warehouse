package com.hungnguyen.srs_warehouse.dto.orderDispatch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDTO {
    private String warehouseId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer capacity;

    // Constructor mặc định
    public WarehouseDTO() {}

    // Constructor đầy đủ
    public WarehouseDTO(String warehouseId, String name, Double latitude, Double longitude, Integer capacity) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
    }
}
