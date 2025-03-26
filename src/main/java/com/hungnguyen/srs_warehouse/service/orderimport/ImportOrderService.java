package com.hungnguyen.srs_warehouse.service.orderimport;

import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.model.DTO.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.mapper.OrderCreateMapper;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ImportOrderService {

    private final OrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ReceiverRepository receiverRepository;
    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderCreateMapper orderCreateMapper;
    private final MessageSource messageSource;
    private final OrderCounterRepository orderCounterRepository;
    private final JwtUtils jwtUtils;

    @Autowired
    public ImportOrderService(OrderRepository orderRepository,
                              SupplierRepository supplierRepository,
                              ReceiverRepository receiverRepository,
                              UserRepository userRepository,
                              WarehouseRepository warehouseRepository,
                              OrderCreateMapper orderCreateMapper,
                              MessageSource messageSource,
                              OrderCounterRepository orderCounterRepository,
                              JwtUtils jwtUtils) {
        this.orderRepository = orderRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.orderCreateMapper = orderCreateMapper;
        this.messageSource = messageSource;
        this.jwtUtils = jwtUtils;
        this.orderCounterRepository = orderCounterRepository;
    }

    @Transactional
    public BaseResponseDTO<?> importOrders(MultipartFile file, String token) {
        List<Map<String, String>> errorRows = new ArrayList<>();
        List<OrderRequest> validOrders = ExcelUtils.parseExcelFile(file, errorRows);

        if (!errorRows.isEmpty()) {
            String errorFilePath = ExcelUtils.generateErrorFile(file, errorRows);
            return new BaseResponseDTO<>(0, getMessage("EXCEL_IMPORT_ERROR"), errorFilePath);
        }

        // ✅ Lấy thông tin user từ JWT Token
        String username = jwtUtils.getUsernameFromToken(token);
        String warehouseId = jwtUtils.getWarehouseIdFromToken(token);

        if (warehouseId == null || warehouseId.isEmpty()) {
            return new BaseResponseDTO<>(0, getMessage("WAREHOUSE_NOT_FOUND"), null);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(getMessage("USER_001")));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException(getMessage("WAREHOUSE_NOT_FOUND")));

        List<String> orderIds = new ArrayList<>();
        for (OrderRequest dto : validOrders) {
            Order order = processOrderRequest(dto, user, warehouse);
            orderRepository.save(order);
            orderIds.add(order.getOrderId());
        }

        return new BaseResponseDTO<>(1, getMessage("SUCCESS"), orderIds);
    }

    private Order processOrderRequest(OrderRequest dto, User user, Warehouse warehouse) {
        Supplier supplier = supplierRepository.findByNameAndPhone(dto.supplier().name(), dto.supplier().phone())
                .orElseGet(() -> {
                    Supplier newSupplier = orderCreateMapper.toSupplier(dto.supplier());
                    newSupplier.setSupplierId(generateSupplierId());
                    return supplierRepository.save(newSupplier);
                });

        Receiver receiver = receiverRepository.findByNameAndPhone(dto.receiver().name(), dto.receiver().phone())
                .orElseGet(() -> {
                    Receiver newReceiver = orderCreateMapper.toReceiver(dto.receiver());
                    newReceiver.setReceiverId(generateReceiverId());
                    return receiverRepository.save(newReceiver);
                });

        String orderId = generateOrderId();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setSupplier(supplier);
        order.setReceiver(receiver);
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setCreatedBy(user.getUserId());
        order.setWarehouse(warehouse);
        order.setStatus(0);

        return order;
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

    private String generateOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int sequence = getNextOrderNumber();
        return String.format("DH-%s-%05d", datePart, sequence);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
