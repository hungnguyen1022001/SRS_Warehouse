package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderDetail.WarehouseDTO;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.model.Warehouse;
import com.hungnguyen.srs_warehouse.repository.WarehouseRepository;
import com.hungnguyen.srs_warehouse.service.WarehouseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public BaseResponseDTO<List<String>> getAllWarehouseIds() {
        return Optional.ofNullable(warehouseRepository.findAllWarehouseIds())
                .filter(ids -> !ids.isEmpty())
                .map(ids -> BaseResponseDTO.success("SUCCESS", ids))
                .orElseThrow(() ->  new CustomExceptions.NotFoundException("WAREHOUSE_NOT_FOUND"));
    }


    @Override
    public BaseResponseDTO<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseRepository.findAll().stream()
                .map(w -> WarehouseDTO.builder()
                        .warehouseId(w.getWarehouseId())
                        .name(w.getName())
                        .address(w.getAddress())
                        .build())
                .collect(Collectors.toList());

        return warehouses.isEmpty()
                ? BaseResponseDTO.fail("WAREHOUSE_NOT_FOUND")
                : BaseResponseDTO.success("SUCCESS", warehouses);
    }

    @Override
    public BaseResponseDTO<WarehouseDTO> getWarehouseById(String warehouseId) {
        Warehouse warehouse = warehouseRepository.findByWarehouseId(warehouseId);
        if (warehouse == null) {
            return BaseResponseDTO.fail("WAREHOUSE_NOT_FOUND");
        }

        WarehouseDTO dto = WarehouseDTO.builder()
                .warehouseId(warehouse.getWarehouseId())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .build();

        return BaseResponseDTO.success("SUCCESS", dto);
    }
}
