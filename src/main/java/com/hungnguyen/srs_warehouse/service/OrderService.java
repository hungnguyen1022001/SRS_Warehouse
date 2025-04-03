package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListDTO;

import java.util.List;
import java.util.Map;

public interface OrderService {
    BaseResponseDTO<Map<String, Object>> getOrderList(String orderId, String phone, Integer status, String warehouseId, Integer page,Integer limit);

    BaseResponseDTO<String> createOrder(OrderRequest request);
    BaseResponseDTO<OrderDetailDTO> getOrderDetail(String orderId);
    BaseResponseDTO<List<String>> getOrderIds(String orderId);
}
