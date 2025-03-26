package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.model.DTO.OrderListResponse;
import com.hungnguyen.srs_warehouse.model.DTO.orderDetail.*;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.model.DTO.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.mapper.OrderCreateMapper;
import com.hungnguyen.srs_warehouse.mapper.OrderMapper;
import com.hungnguyen.srs_warehouse.mapper.OrderDetailMapper;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.specification.OrderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Locale;
import java.util.Map;
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
    private final OrderCreateMapper orderCreateMapper;
    private final OrderMapper orderMapper;
    private final MessageSource messageSource;
    private final JwtUtils jwtUtils;
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
                        MessageSource messageSource,
                        JwtUtils jwtUtils,
                        OrderDetailMapper orderDetailMapper,
                        OrderCounterRepository orderCounterRepository) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.orderCreateMapper = orderCreateMapper;
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.messageSource = messageSource;
        this.jwtUtils = jwtUtils;
        this.orderCounterRepository = orderCounterRepository;
    }

    public Map<String, Object> getOrderList(OrderSearchCriteria criteria) {
        Specification<Order> spec = OrderSpecification.withSearchCriteria(criteria);
        Page<Order> orderPage = orderRepository.findAll(spec, PageRequest.of(criteria.getPage(), criteria.getSize()));

        boolean isSearchingByIdOrPhone = (criteria.getOrderId() != null && !criteria.getOrderId().isEmpty())
                || (criteria.getPhone() != null && !criteria.getPhone().isEmpty());

        if (isSearchingByIdOrPhone && orderPage.isEmpty()) {
            return Map.of("status", 0, "message", getMessage("ORDER_001"));
        }

        return Map.of(
                "status", orderPage.isEmpty() ? 0 : 1,
                "message", getMessage(orderPage.isEmpty() ? "ORDER_001" : "SUCCESS"),
                "data", new OrderListResponse(
                        orderPage.getContent().stream().map(orderMapper::toOrderListDTO).collect(Collectors.toList()),
                        orderPage.getTotalElements(),
                        criteria.getPage(),
                        criteria.getSize()
                )
        );
    }



    @Transactional
    public BaseResponseDTO<String> createOrder(OrderRequest request, String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            String warehouseId = jwtUtils.getWarehouseIdFromToken(token);

            if (warehouseId == null || warehouseId.isEmpty()) {
                return new BaseResponseDTO<>(0, getMessage("ORDER_001"), null);
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("USER_001"));

            // 🔍 Kiểm tra nhà cung cấp theo số điện thoại
            Optional<Supplier> existingSupplier = supplierRepository.findByPhone(request.supplier().phone());
            if (existingSupplier.isPresent() && !existingSupplier.get().getName().equals(request.supplier().name())) {
                return new BaseResponseDTO<>(0, getMessage("ORDER_002"), null);
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

            // 🔍 Kiểm tra người nhận theo số điện thoại
            Optional<Receiver> existingReceiver = receiverRepository.findByPhone(request.receiver().phone());
            if (existingReceiver.isPresent() && !existingReceiver.get().getName().equals(request.receiver().name())) {
                return new BaseResponseDTO<>(0, getMessage("ORDER_003"), null);
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

            return new BaseResponseDTO<>(1, getMessage("SUCCESS"), orderId);

        } catch (RuntimeException ex) {
            return new BaseResponseDTO<>(0, getMessage(ex.getMessage()), null);
        } catch (Exception ex) {
            return new BaseResponseDTO<>(0, getMessage("SERVER_ERROR"), null);
        }
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

    private String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }
    public BaseResponseDTO<OrderDetailDTO> getOrderDetail(String orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            return new BaseResponseDTO<>(0, getMessage("ORDER_001"), null);
        }

        OrderDetailDTO orderDetailDTO = orderDetailMapper.toOrderDetailDTO(optionalOrder.get());
        return new BaseResponseDTO<>(1, getMessage("SUCCESS"), orderDetailDTO);
    }

    public BaseResponseDTO<List<String>> getOrderIds(String orderId) {
        List<String> orderIds;

        if (orderId != null && !orderId.isEmpty()) {
            // Nếu có orderId, tìm những mã đơn hàng chứa orderId
            orderIds = orderRepository.findByOrderIdContaining(orderId)
                    .stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
        } else {
            // Nếu không có orderId, lấy toàn bộ mã đơn hàng
            orderIds = orderRepository.findAll()
                    .stream()
                    .map(Order::getOrderId)
                    .collect(Collectors.toList());
        }

        if (orderIds.isEmpty()) {
            return new BaseResponseDTO<>(0, getMessage("ORDER_001"), null);
        }

        return new BaseResponseDTO<>(1, getMessage("SUCCESS"), orderIds);
    }

}
