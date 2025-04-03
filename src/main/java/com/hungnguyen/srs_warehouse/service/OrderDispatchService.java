package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;

public interface OrderDispatchService {
    BaseResponseDTO<String> processOrders(String username, boolean isBatchJob);
}
