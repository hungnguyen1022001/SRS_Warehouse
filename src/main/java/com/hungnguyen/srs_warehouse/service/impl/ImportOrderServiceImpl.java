package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.mapper.OrderCreateMapper;
import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.security.jwt.*;
import com.hungnguyen.srs_warehouse.service.ImportOrderService;
import com.hungnguyen.srs_warehouse.util.ExcelUtils;
import com.hungnguyen.srs_warehouse.util.IdGeneratorUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportOrderServiceImpl implements ImportOrderService {

    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final SupplierRepository supplierRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderCreateMapper orderCreateMapper;
    private final JwtUtils jwtUtils;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final IdGeneratorUtil idGeneratorUtil;

    public ImportOrderServiceImpl(OrderRepository orderRepository,
                                  OrderHistoryRepository orderHistoryRepository,
                                  SupplierRepository supplierRepository,
                                  ReceiverRepository receiverRepository,
                                  UserRepository userRepository,
                                  WarehouseRepository warehouseRepository,
                                  OrderCreateMapper orderCreateMapper,
                                  JwtUtils jwtUtils,
                                  JwtAuthenticationFilter jwtAuthenticationFilter,
                                  IdGeneratorUtil idGeneratorUtil) {
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.orderCreateMapper = orderCreateMapper;
        this.jwtUtils = jwtUtils;
        this.jwtAuthenticationFilter= jwtAuthenticationFilter;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional
    @Override
    public BaseResponseDTO<?> importOrders(MultipartFile file, HttpServletRequest request) {
        String token = jwtAuthenticationFilter.extractToken(request);

        String username = jwtUtils.getUsernameFromToken(token);
        String warehouseId = jwtUtils.getWarehouseIdFromToken(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("USER_NOT_FOUND"));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new CustomExceptions.NotFoundException("WAREHOUSE_NOT_FOUND"));

        List<Map<String, String>> errorRows = new ArrayList<>();
        List<OrderRequest> validOrders = ExcelUtils.parseExcelFile(file, errorRows);

        if (!errorRows.isEmpty()) {
            String errorFilePath = ExcelUtils.generateErrorFile(file, errorRows);
            return BaseResponseDTO.fail("EXCEL_IMPORT_ERROR", errorFilePath);
        }

        // ✅ Lưu đơn hàng
        List<String> orderIds = validOrders.stream()
                .map(dto -> saveOrder(dto, user, warehouse))
                .collect(Collectors.toList());

        return BaseResponseDTO.success("SUCCESS", orderIds);
    }


    private String saveOrder(OrderRequest dto, User user, Warehouse warehouse) {
        Supplier supplier = supplierRepository.findByNameAndPhone(dto.supplier().name(), dto.supplier().phone())
                .orElseGet(() -> {
                    Supplier newSupplier = orderCreateMapper.toSupplier(dto.supplier());
                    newSupplier.setSupplierId(idGeneratorUtil.generateSupplierId());
                    return supplierRepository.save(newSupplier);
                });

        Receiver receiver = receiverRepository.findByNameAndPhone(dto.receiver().name(), dto.receiver().phone())
                .orElseGet(() -> {
                    Receiver newReceiver = orderCreateMapper.toReceiver(dto.receiver());
                    newReceiver.setReceiverId(idGeneratorUtil.generateReceiverId());
                    return receiverRepository.save(newReceiver);
                });

        String orderId = idGeneratorUtil.generateOrderId();
        Order order = Order.builder()
                .orderId(orderId)
                .supplier(supplier)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .createdBy(user.getUserId())
                .warehouse(warehouse)
                .failedDeliveries(0)
                .status(0)
                .build();

        orderRepository.save(order);

        order = orderRepository.findById(orderId).orElseThrow(() ->
                new CustomExceptions.NotFoundException("ORDER_001"));

        OrderHistory orderHistory = OrderHistory.builder()
                .historyId(idGeneratorUtil.generateOrderHistoryId())
                .performedBy(user)
                .performedAt(LocalDateTime.now())
                .order(order)
                .warehouse(warehouse)
                .status(0)
                .version(1)
                .build();

        orderHistoryRepository.saveAndFlush(orderHistory);

        return orderId;
    }


}
