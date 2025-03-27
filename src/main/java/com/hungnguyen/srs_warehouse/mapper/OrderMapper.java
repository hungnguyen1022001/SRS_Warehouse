package com.hungnguyen.srs_warehouse.mapper;

import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "warehouse.warehouseId", target = "warehouseId")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "supplier.address", target = "supplierAddress")
    @Mapping(source = "supplier.phone", target = "supplierPhone")
    @Mapping(source = "supplier.email", target = "supplierEmail")
    @Mapping(source = "receiver.name", target = "receiverName")
    @Mapping(source = "receiver.address", target = "receiverAddress")
    @Mapping(source = "receiver.phone", target = "receiverPhone")
    @Mapping(source = "receiver.email", target = "receiverEmail")
    OrderListDTO toOrderListDTO(Order order);

    List<OrderListDTO> toOrderListDTOs(List<Order> orders);
}
