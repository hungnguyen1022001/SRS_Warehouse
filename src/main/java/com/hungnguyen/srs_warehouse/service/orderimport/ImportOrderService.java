package com.hungnguyen.srs_warehouse.service.orderimport;

import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
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
    private final JwtUtils jwtUtils;

    @Autowired
    public ImportOrderService(OrderRepository orderRepository,
                              SupplierRepository supplierRepository,
                              ReceiverRepository receiverRepository,
                              UserRepository userRepository,
                              WarehouseRepository warehouseRepository,
                              OrderCreateMapper orderCreateMapper,
                              MessageSource messageSource,
                              JwtUtils jwtUtils) {
        this.orderRepository = orderRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.orderCreateMapper = orderCreateMapper;
        this.messageSource = messageSource;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public Map<String, Object> importOrders(MultipartFile file, String token) {
        List<Map<String, String>> errorRows = new ArrayList<>();
        List<OrderRequest> validOrders = ExcelUtils.parseExcelFile(file, errorRows);

        if (!errorRows.isEmpty()) {
            String errorFilePath = ExcelUtils.generateErrorFile(file, errorRows);
            return Map.of("status", 0, "message", getMessage("FAIL"), "errorFile", errorFilePath);
        }

        // ✅ Lấy thông tin user từ JWT Token
        String username = jwtUtils.getUsernameFromToken(token);
        String warehouseId = jwtUtils.getWarehouseIdFromToken(token);

        if (warehouseId == null || warehouseId.isEmpty()) {
            throw new RuntimeException("Không tìm thấy warehouseId trong token!");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + username));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho hàng: " + warehouseId));

        for (OrderRequest dto : validOrders) {
            Order order = processOrderRequest(dto, user, warehouse);
            orderRepository.save(order);  // ✅ Lưu từng order một
        }

        return Map.of("status", 1, "message", getMessage("SUCCESS"));
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

        Order order = new Order();
        order.setOrderId(generateOrderId());
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

    private synchronized String generateOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        long count = orderRepository.count() + 1;
        return String.format("DH-%s-%05d", datePart, count);
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
