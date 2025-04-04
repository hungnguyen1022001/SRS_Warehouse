package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderCreate.*;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListDTO;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.mapper.OrderDetailMapper;
import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.mapper.OrderMapper;
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
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderHistoryRepository orderHistoryRepository,
                            SupplierRepository supplierRepository,
                            ReceiverRepository receiverRepository,
                            WarehouseRepository warehouseRepository,
                            UserRepository userRepository,
                            OrderDetailMapper orderDetailMapper,
                            OrderMapper orderMapper,
                            IdGeneratorUtil idGeneratorUtil) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.orderDetailMapper = orderDetailMapper;
        this.orderMapper = orderMapper;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Override
    public BaseResponseDTO<Page<OrderListDTO>> getOrderList(String orderId, String phone, Integer status,
                                                            String warehouseId, Integer page, Integer limit) {


        Pageable pageable = PageRequest.of(page, limit);
        Page<Order> orderPage = orderRepository.searchOrders(orderId, phone, status, warehouseId, pageable);

        if (orderPage.isEmpty()) {
            throw new CustomExceptions.NotFoundException("ORDER_001");
        }

        Page<OrderListDTO> orderListDTOPage = orderPage.map(orderMapper::toOrderListDTO);

        return BaseResponseDTO.success("SUCCESS", orderListDTOPage);
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
        SupplierRequest supplierRequest = request.supplier();
        return supplierRepository.findByPhone(supplierRequest.phone())
                .orElseGet(() -> createNewSupplier(supplierRequest));
    }

    private Supplier createNewSupplier(SupplierRequest request) {
        return supplierRepository.save(Supplier.builder()
                .supplierId(idGeneratorUtil.generateSupplierId())
                .name(request.name())
                .address(request.address())
                .phone(request.phone())
                .email(request.email())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build());
    }

    private Receiver findOrCreateReceiver(OrderRequest request) {
        ReceiverRequest receiverRequest = request.receiver();
        return receiverRepository.findByPhone(receiverRequest.phone())
                .orElseGet(() -> createNewReceiver(receiverRequest));
    }

    private Receiver createNewReceiver(ReceiverRequest request) {
        return receiverRepository.save(Receiver.builder()
                .receiverId(idGeneratorUtil.generateReceiverId())
                .name(request.name())
                .address(request.address())
                .phone(request.phone())
                .email(request.email())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build());
    }

}
