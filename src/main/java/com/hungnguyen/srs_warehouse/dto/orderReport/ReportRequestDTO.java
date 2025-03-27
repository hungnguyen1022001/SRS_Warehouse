package com.hungnguyen.srs_warehouse.dto.orderReport;

import java.time.LocalDate;
import java.util.List;

public record ReportRequestDTO(List<String> warehouseIds, LocalDate startDate, LocalDate endDate, boolean reportByMonth) {}
