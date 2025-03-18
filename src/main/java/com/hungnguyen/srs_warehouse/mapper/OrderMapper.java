package com.hungnguyen.srs_warehouse.mapper;


import com.hungnguyen.srs_warehouse.model.DTO.OrderListDTO;
import com.hungnguyen.srs_warehouse.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderListDTO toOrderListDTO(Order order) {
        OrderListDTO dto = new OrderListDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());

        // Warehouse info
        if (order.getWarehouse() != null) {
            dto.setWarehouseId(order.getWarehouse().getWarehouseId());
            dto.setWarehouseName(order.getWarehouse().getName());
        }

        // Supplier info
        if (order.getSupplier() != null) {
            dto.setSupplierName(order.getSupplier().getName());
            dto.setSupplierAddress(order.getSupplier().getAddress());
            dto.setSupplierPhone(order.getSupplier().getPhone());
            dto.setSupplierEmail(order.getSupplier().getEmail());
        }

        // Receiver info
        if (order.getReceiver() != null) {
            dto.setReceiverName(order.getReceiver().getName());
            dto.setReceiverAddress(order.getReceiver().getAddress());
            dto.setReceiverPhone(order.getReceiver().getPhone());
            dto.setReceiverEmail(order.getReceiver().getEmail());
        }

        // Order dates
        dto.setStoredAt(order.getStoredAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setFailedDeliveries(order.getFailedDeliveries());
        dto.setReturnAt(order.getReturnAt());

        return dto;
    }
}
