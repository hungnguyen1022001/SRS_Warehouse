package com.hungnguyen.srs_warehouse.mapper;

import com.hungnguyen.srs_warehouse.dto.orderDetail.*;
import com.hungnguyen.srs_warehouse.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDetailMapper {

    // Chuyển Order entity sang OrderDetailDTO
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    @Mapping(target = "storedAt", source = "storedAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    @Mapping(target = "deliveredAt", source = "deliveredAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    @Mapping(target = "returnAt", source = "returnAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    @Mapping(target = "warehouse", source = "warehouse")
    @Mapping(target = "supplier", source = "supplier")
    @Mapping(target = "receiver", source = "receiver")
    @Mapping(target = "orderHistories", source = "orderHistories")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "failedDeliveries", source = "failedDeliveries")
    OrderDetailDTO toOrderDetailDTO(Order order);

    WarehouseDTO toWarehouseDTO(Warehouse warehouse);

    SupplierDTO toSupplierDTO(Supplier supplier);

    ReceiverDTO toReceiverDTO(Receiver receiver);

    @Mapping(target = "performedAt", source = "performedAt", qualifiedByName = "mapLocalDateTimeToTimestamp")
    OrderHistoryDTO toOrderHistoryDTO(OrderHistory orderHistory);

    List<OrderHistoryDTO> toOrderHistoryDTOList(List<OrderHistory> orderHistories);

    @Named("mapLocalDateTimeToTimestamp")
    default Timestamp mapLocalDateTimeToTimestamp(LocalDateTime value) {
        return value != null ? Timestamp.valueOf(value) : null;
    }
}
