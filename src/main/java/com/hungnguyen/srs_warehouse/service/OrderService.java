package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderDetailDTO;
import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderListResponse;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderList.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.mapper.OrderCreateMapper;
import com.hungnguyen.srs_warehouse.mapper.OrderMapper;
import com.hungnguyen.srs_warehouse.mapper.OrderDetailMapper;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.specification.OrderSpecification;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final SupplierRepository supplierRepository;
    private final ReceiverRepository receiverRepository;
    private final OrderMapper orderMapper;
    private final OrderCounterRepository orderCounterRepository;
    private final OrderDetailMapper orderDetailMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderHistoryRepository orderHistoryRepository,
                        SupplierRepository supplierRepository,
                        ReceiverRepository receiverRepository,
                        WarehouseRepository warehouseRepository,
                        UserRepository userRepository,
                        OrderCreateMapper orderCreateMapper,
                        OrderMapper orderMapper,
                        JwtUtils jwtUtils,
                        OrderDetailMapper orderDetailMapper,
                        OrderCounterRepository orderCounterRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.orderCounterRepository = orderCounterRepository;
    }

    public BaseResponseDTO<OrderListResponse> getOrderList(OrderSearchCriteria criteria) {
        Specification<Order> spec = OrderSpecification.withSearchCriteria(criteria);
        Page<Order> orderPage = orderRepository.findAll(spec, PageRequest.of(criteria.getPage(), criteria.getSize()));

        if (orderPage.isEmpty()) {
            return BaseResponseDTO.fail("ORDER_001");
        }

        OrderListResponse response = new OrderListResponse(
                orderMapper.toOrderListDTOs(orderPage.getContent()),
                orderPage.getTotalElements(),
                criteria.getPage(),
                criteria.getSize()
        );

        return BaseResponseDTO.success("SUCCESS", response);
    }


    @Transactional
    public BaseResponseDTO<String> createOrder(OrderRequest request) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return BaseResponseDTO.fail("AUTH_001");
            }

            Object principal = authentication.getPrincipal();
            String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();


            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("USER_001"));

            String warehouseId = user.getWarehouse() != null ? user.getWarehouse().getWarehouseId() : null;
            if (warehouseId == null || warehouseId.isEmpty()) {
                return BaseResponseDTO.fail("ORDER_001");
            }

            Optional<Supplier> existingSupplier = supplierRepository.findByPhone(request.supplier().phone());
            if (existingSupplier.isPresent() && !existingSupplier.get().getName().equals(request.supplier().name())) {
                return BaseResponseDTO.fail("ORDER_002");
            }

            Supplier supplier = existingSupplier.orElseGet(() -> {
                Supplier newSupplier = new Supplier();
                newSupplier.setSupplierId(generateSupplierId());
                newSupplier.setName(request.supplier().name());
                newSupplier.setAddress(request.supplier().address());
                newSupplier.setPhone(request.supplier().phone());
                newSupplier.setEmail(request.supplier().email());
                newSupplier.setLatitude(request.supplier().latitude());
                newSupplier.setLongitude(request.supplier().longitude());
                return supplierRepository.save(newSupplier);
            });

            Optional<Receiver> existingReceiver = receiverRepository.findByPhone(request.receiver().phone());
            if (existingReceiver.isPresent() && !existingReceiver.get().getName().equals(request.receiver().name())) {
                return BaseResponseDTO.fail("ORDER_003");
            }

            Receiver receiver = existingReceiver.orElseGet(() -> {
                Receiver newReceiver = new Receiver();
                newReceiver.setReceiverId(generateReceiverId());
                newReceiver.setName(request.receiver().name());
                newReceiver.setAddress(request.receiver().address());
                newReceiver.setPhone(request.receiver().phone());
                newReceiver.setEmail(request.receiver().email());
                newReceiver.setLatitude(request.receiver().latitude());
                newReceiver.setLongitude(request.receiver().longitude());
                return receiverRepository.save(newReceiver);
            });

            String orderId = generateOrderId();

            Order order = new Order();
            order.setOrderId(orderId);
            order.setSupplier(supplier);
            order.setReceiver(receiver);
            order.setCreatedAt(LocalDateTime.now());
            order.setCreatedBy(user.getUserId());
            order.setStatus(0);
            order = orderRepository.save(order);

            Warehouse warehouse = warehouseRepository.findById(warehouseId)
                    .orElseThrow(() -> new RuntimeException("ORDER_001"));

            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setHistoryId(generateOrderHistoryId());
            orderHistory.setPerformedBy(user);
            orderHistory.setPerformedAt(LocalDateTime.now());
            orderHistory.setOrder(order);
            orderHistory.setWarehouse(warehouse);
            orderHistory.setStatus(0);
            orderHistory.setVersion(1);
            orderHistoryRepository.saveAndFlush(orderHistory);

            return BaseResponseDTO.success("SUCCESS", orderId);

        } catch (RuntimeException ex) {
            return BaseResponseDTO.fail(ex.getMessage());
        } catch (Exception ex) {
            return BaseResponseDTO.fail("SERVER_ERROR");
        }
    }

    public BaseResponseDTO<OrderDetailDTO> getOrderDetail(String orderId) {
        return orderRepository.findById(orderId)
                .map(order -> BaseResponseDTO.success("SUCCESS", orderDetailMapper.toOrderDetailDTO(order)))
                .orElseGet(() -> BaseResponseDTO.fail("ORDER_001"));
    }

    public BaseResponseDTO<List<String>> getOrderIds(String orderId) {
        List<String> orderIds;

        if (orderId != null && !orderId.isEmpty()) {
            orderIds = orderRepository.findByOrderIdContaining(orderId)
                    .stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
        } else {
            orderIds = orderRepository.findAll()
                    .stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
        }

        if (orderIds.isEmpty()) {
            return BaseResponseDTO.fail("ORDER_001");
        }

        return BaseResponseDTO.success("SUCCESS", orderIds);
    }

    private synchronized String generateSupplierId() {
        long count = supplierRepository.count() + 1;
        return String.format("SUP-%03d", count);
    }

    private synchronized String generateReceiverId() {
        long count = receiverRepository.count() + 1;
        return String.format("REC-%03d", count);
    }

    private synchronized int getNextOrderNumber() {
        String today = LocalDate.now().toString();
        OrderCounter counter = orderCounterRepository.findById(today)
                .orElseGet(() -> new OrderCounter(today, 0));

        counter.setCounter(counter.getCounter() + 1);
        orderCounterRepository.save(counter);

        return counter.getCounter();
    }

    private synchronized String generateOrderHistoryId() {
        long count = orderHistoryRepository.count() + 1;
        return String.format("HIS-%05d", count);
    }

    private String generateOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int sequence = getNextOrderNumber();
        return String.format("DH-%s-%05d", datePart, sequence);
    }
}
