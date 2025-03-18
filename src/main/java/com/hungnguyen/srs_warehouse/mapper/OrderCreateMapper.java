package com.hungnguyen.srs_warehouse.mapper;

import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.ReceiverRequest;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.SupplierRequest;
import com.hungnguyen.srs_warehouse.model.Supplier;
import com.hungnguyen.srs_warehouse.model.Receiver;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderCreateMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Supplier toSupplier(SupplierRequest dto);
    Receiver toReceiver(ReceiverRequest dto);
}
