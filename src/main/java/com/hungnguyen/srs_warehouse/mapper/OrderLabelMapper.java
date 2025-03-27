package com.hungnguyen.srs_warehouse.mapper;

import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import com.hungnguyen.srs_warehouse.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderLabelMapper {
    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "order.supplier", target = "supplier")
    @Mapping(source = "order.receiver", target = "receiver")
    @Mapping(source = "order.warehouse", target = "warehouse")
    @Mapping(source = "order.createdAt", dateFormat = "yyyy-MM-dd HH:mm", target = "createdAt")
    @Mapping(source = "order.status", target = "status")
    OrderLabelDTO toDto(Order order);
}

