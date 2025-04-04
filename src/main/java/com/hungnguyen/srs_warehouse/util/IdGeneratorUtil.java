package com.hungnguyen.srs_warehouse.util;
import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.repository.SupplierRepository;
import com.hungnguyen.srs_warehouse.repository.ReceiverRepository;
import com.hungnguyen.srs_warehouse.repository.OrderHistoryRepository;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;

import org.springframework.stereotype.Component;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Component
public class IdGeneratorUtil {

    private final SupplierRepository supplierRepository;
    private final ReceiverRepository receiverRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    public IdGeneratorUtil(SupplierRepository supplierRepository, ReceiverRepository receiverRepository,
                           OrderHistoryRepository orderHistoryRepository, OrderRepository orderRepository) {
        this.supplierRepository = supplierRepository;
        this.receiverRepository = receiverRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderRepository = orderRepository;
    }

    // Generate Supplier ID
    public String generateSupplierId() {
        long count = supplierRepository.count() + 1;
        return String.format("SUP-%03d", count);
    }

    // Generate Receiver ID
    public String generateReceiverId() {
        long count = receiverRepository.count() + 1;
        return String.format("REC-%03d", count);
    }

    // Generate Order History ID
    public String generateOrderHistoryId() {
        long count = orderHistoryRepository.count() + 1;
        return String.format("HIS-%05d", count);
    }

    // Generate Order ID
    public String generateOrderId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        Order latestOrder = orderRepository.findTopByOrderByOrderIdDesc();

        if (latestOrder != null) {
            String latestOrderDatePart = latestOrder.getOrderId().substring(3, 9);

            if (datePart.equals(latestOrderDatePart)) {
                int sequence = Integer.parseInt(latestOrder.getOrderId().substring(10)) + 1;
                return "DH-%s-%05d".formatted(datePart, sequence);
            }
        }

        return "DH-%s-%05d".formatted(datePart, 1);
    }
}
