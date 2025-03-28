package com.hungnguyen.srs_warehouse.dto.orderList;

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
    private String warehouseId;
    private int page = 0;
    private int size = 10;
}