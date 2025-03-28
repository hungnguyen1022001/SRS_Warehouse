package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderDetail.WarehouseDTO;
import com.hungnguyen.srs_warehouse.model.Warehouse;
import com.hungnguyen.srs_warehouse.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
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

    public BaseResponseDTO<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseRepository.findAll().stream()
                .map(w -> new WarehouseDTO(w.getWarehouseId(), w.getName(), w.getAddress()))
                .collect(Collectors.toList());

        return warehouses.isEmpty()
                ? BaseResponseDTO.fail("WAREHOUSE_NOT_FOUND")
                : BaseResponseDTO.success("SUCCESS", warehouses);
    }

    public BaseResponseDTO<WarehouseDTO> getWarehouseById(String warehouseId) {
        Warehouse warehouse = warehouseRepository.findByWarehouseId(warehouseId);
        if (warehouse == null) {
            return BaseResponseDTO.fail("WAREHOUSE_NOT_FOUND");
        }

        WarehouseDTO dto = new WarehouseDTO(warehouse.getWarehouseId(), warehouse.getName(), warehouse.getAddress());
        return BaseResponseDTO.success("SUCCESS", dto);
    }
}
