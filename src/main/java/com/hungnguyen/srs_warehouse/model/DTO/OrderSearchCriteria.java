package com.hungnguyen.srs_warehouse.model.DTO;

import lombok.Data;
import java.util.List;

@Data
public class OrderSearchCriteria {
    private String orderId;
    private String phone;
    private Integer status;
    private List<String> warehouseIds;
    private int page = 0;
    private int size = 10;
}