package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.Warehouse;
import com.hungnguyen.srs_warehouse.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;

import java.util.List;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public BaseResponseDTO<List<String>> getAllWarehouseIds() {
        List<String> warehouseIds = warehouseRepository.findAllWarehouseIds();
        return warehouseIds.isEmpty()
                ? BaseResponseDTO.fail("WAREHOUSE_NOT_FOUND")
                : BaseResponseDTO.success("SUCCESS", warehouseIds);
    }
   
    
}
