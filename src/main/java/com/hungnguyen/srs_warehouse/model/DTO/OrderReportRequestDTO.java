package com.hungnguyen.srs_warehouse.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderReportRequestDTO {
    private List<String> warehouseIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isMonthly;
}
