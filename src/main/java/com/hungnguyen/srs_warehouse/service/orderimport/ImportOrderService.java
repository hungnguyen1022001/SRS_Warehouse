package com.hungnguyen.srs_warehouse.service.orderimport;

import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.mapper.OrderCreateMapper;
import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.model.Supplier;
import com.hungnguyen.srs_warehouse.model.Receiver;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.repository.SupplierRepository;
import com.hungnguyen.srs_warehouse.repository.ReceiverRepository;
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
    private final OrderCreateMapper orderCreateMapper;
    private final MessageSource messageSource;

    @Autowired
    public ImportOrderService(OrderRepository orderRepository,
                              SupplierRepository supplierRepository,
                              ReceiverRepository receiverRepository,
                              OrderCreateMapper orderCreateMapper,
                              MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.orderCreateMapper = orderCreateMapper;
        this.messageSource = messageSource;
    }

    @Transactional
    public Map<String, Object> importOrders(MultipartFile file) {
        List<Map<String, String>> errorRows = new ArrayList<>();
        List<OrderRequest> validOrders = ExcelUtils.parseExcelFile(file, errorRows);

        if (!errorRows.isEmpty()) {
            String errorFilePath = ExcelUtils.generateErrorFile(file,errorRows);
            return Map.of("status", 0, "message", getMessage("FAIL"), "errorFile", errorFilePath);
        }

        List<Order> ordersToSave = new ArrayList<>();
        for (OrderRequest dto : validOrders) {
            ordersToSave.add(processOrderRequest(dto));
        }

        orderRepository.saveAll(ordersToSave);
        return Map.of("status", 1, "message", getMessage("SUCCESS"));
    }

    private Order processOrderRequest(OrderRequest dto) {
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
