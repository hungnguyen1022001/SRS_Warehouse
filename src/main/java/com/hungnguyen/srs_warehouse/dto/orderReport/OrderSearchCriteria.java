package com.hungnguyen.srs_warehouse.dto.orderReport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchCriteria {
    private String orderId;
    private String phone;
    private Integer status;
    private List<String> warehouseIds;
    private int page = 0;
    private int size = 10;
}