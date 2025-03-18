package com.hungnguyen.srs_warehouse.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {
    private List<OrderListDTO> orders;
    private long total;
    private int page;
    private int size;
}

