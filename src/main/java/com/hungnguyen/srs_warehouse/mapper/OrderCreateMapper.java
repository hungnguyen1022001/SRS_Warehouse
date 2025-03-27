package com.hungnguyen.srs_warehouse.mapper;

import com.hungnguyen.srs_warehouse.dto.orderCreate.ReceiverRequest;
import com.hungnguyen.srs_warehouse.dto.orderCreate.SupplierRequest;
import com.hungnguyen.srs_warehouse.model.Supplier;
import com.hungnguyen.srs_warehouse.model.Receiver;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderCreateMapper {
    OrderCreateMapper INSTANCE = Mappers.getMapper(OrderCreateMapper.class);

    Supplier toSupplier(SupplierRequest dto);
    Receiver toReceiver(ReceiverRequest dto);
}
