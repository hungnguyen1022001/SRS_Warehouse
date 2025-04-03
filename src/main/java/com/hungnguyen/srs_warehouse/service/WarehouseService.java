package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderDetail.WarehouseDTO;

import java.util.List;

public interface WarehouseService {
    BaseResponseDTO<List<String>> getAllWarehouseIds();
    BaseResponseDTO<List<WarehouseDTO>> getAllWarehouses();
    BaseResponseDTO<WarehouseDTO> getWarehouseById(String warehouseId);
}
