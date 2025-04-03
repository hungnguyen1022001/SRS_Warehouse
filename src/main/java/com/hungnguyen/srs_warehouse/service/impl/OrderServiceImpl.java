package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListDTO;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.mapper.OrderDetailMapper;
import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.service.OrderService;
import com.hungnguyen.srs_warehouse.util.IdGeneratorUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final SupplierRepository supplierRepository;
    private final ReceiverRepository receiverRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final IdGeneratorUtil idGeneratorUtil;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderHistoryRepository orderHistoryRepository,
                            SupplierRepository supplierRepository,
                            ReceiverRepository receiverRepository,
                            WarehouseRepository warehouseRepository,
                            UserRepository userRepository,
                            OrderDetailMapper orderDetailMapper,
                            IdGeneratorUtil idGeneratorUtil) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.orderDetailMapper = orderDetailMapper;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Override
    public BaseResponseDTO<Map<String, Object>> getOrderList(String orderId, String phone, Integer status,
                                                             String warehouseId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Order> orderPage = orderRepository.searchOrders(orderId, phone, status, warehouseId, pageable);

        if (orderPage.isEmpty()) {
            throw new CustomExceptions.NotFoundException("ORDER_001");
        }

        List<OrderListDTO> orders = orderPage.getContent().stream()
                .map(this::mapToOrderListDTO)
                .collect(Collectors.toList());

        return BaseResponseDTO.success("SUCCESS", Map.of(
                "orders", orders,
                "total", orderPage.getTotalElements()
        ));
    }

    @Override
    @Transactional
    public BaseResponseDTO<String> createOrder(OrderRequest request) {
        User user = getAuthenticatedUser();
        Warehouse warehouse = findWarehouseByUser(user);

        Supplier supplier = findOrCreateSupplier(request);
        Receiver receiver = findOrCreateReceiver(request);

        Order order = orderRepository.save(Order.builder()
                .orderId(idGeneratorUtil.generateOrderId())
                .supplier(supplier)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .createdBy(user.getUserId())
                .status(0)
                .failedDeliveries(0)
                .build());

        orderHistoryRepository.saveAndFlush(OrderHistory.builder()
                .historyId(idGeneratorUtil.generateOrderHistoryId())
                .performedBy(user)
                .performedAt(LocalDateTime.now())
                .order(order)
                .warehouse(warehouse)
                .status(0)
                .version(1)
                .build());

        return BaseResponseDTO.success("SUCCESS", order.getOrderId());
    }

    @Override
    public BaseResponseDTO<OrderDetailDTO> getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("ORDER_001"));
        return BaseResponseDTO.success("SUCCESS", orderDetailMapper.toOrderDetailDTO(order));
    }

    @Override
    public BaseResponseDTO<List<String>> getOrderIds(String orderId) {
        List<String> orderIds = Optional.ofNullable(orderId)
                .filter(id -> !id.isEmpty())
                .map(orderRepository::findByOrderIdContaining)
                .orElseGet(orderRepository::findAll)
                .stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            throw new CustomExceptions.NotFoundException("ORDER_001");
        }

        return BaseResponseDTO.success("SUCCESS", orderIds);
    }

    private User getAuthenticatedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .map(principal -> (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString())
                .flatMap(userRepository::findByUsername)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("USER_001"));
    }

    private Warehouse findWarehouseByUser(User user) {
        return Optional.ofNullable(user.getWarehouse())
                .map(Warehouse::getWarehouseId)
                .flatMap(warehouseRepository::findById)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("ORDER_001"));
    }

    private Supplier findOrCreateSupplier(OrderRequest request) {
        return supplierRepository.findByPhone(request.supplier().phone())
                .orElseGet(() -> supplierRepository.save(Supplier.builder()
                        .supplierId(idGeneratorUtil.generateSupplierId())
                        .name(request.supplier().name())
                        .address(request.supplier().address())
                        .phone(request.supplier().phone())
                        .email(request.supplier().email())
                        .latitude(request.supplier().latitude())
                        .longitude(request.supplier().longitude())
                        .build()));
    }

    private Receiver findOrCreateReceiver(OrderRequest request) {
        return receiverRepository.findByPhone(request.receiver().phone())
                .orElseGet(() -> receiverRepository.save(Receiver.builder()
                        .receiverId(idGeneratorUtil.generateReceiverId())
                        .name(request.receiver().name())
                        .address(request.receiver().address())
                        .phone(request.receiver().phone())
                        .email(request.receiver().email())
                        .latitude(request.receiver().latitude())
                        .longitude(request.receiver().longitude())
                        .build()));
    }

    private OrderListDTO mapToOrderListDTO(Order order) {
        return OrderListDTO.builder()
                .orderId(order.getOrderId())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .warehouseId(order.getWarehouseId())
                .warehouseName(order.getWarehouseName())
                .supplierName(order.getSupplierName())
                .supplierAddress(order.getSupplierAddress())
                .supplierPhone(order.getSupplierPhone())
                .supplierEmail(order.getSupplierEmail())
                .receiverName(order.getReceiverName())
                .receiverAddress(order.getReceiverAddress())
                .receiverPhone(order.getReceiverPhone())
                .receiverEmail(order.getReceiverEmail())
                .storedAt(order.getStoredAt())
                .deliveredAt(order.getDeliveredAt())
                .failedDeliveries(order.getFailedDeliveries())
                .returnAt(order.getReturnAt())
                .build();
    }
}
