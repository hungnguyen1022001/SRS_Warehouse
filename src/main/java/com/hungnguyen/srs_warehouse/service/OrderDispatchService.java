package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.util.DistanceCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchService {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Transactional
    public BaseResponseDTO<String> processOrders(String username, boolean isBatchJob) {
        if (isBatchJob) {
            log.info("⚙️ Batch Job dang chay - su dung username: {}", username);
        } else {
            log.info("👤 API dang goi - su dung username: {}", username);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung: " + username));

        List<Order> orders = orderRepository.findTop100ByStatusOrderByCreatedAtAsc(0);
        if (orders.isEmpty()) {
            log.info("📭 Khong co don hang nao can xu ly.");
            return new BaseResponseDTO<>(0, "Khong co don hang nao can dieu phoi.", null);
        }

        List<Warehouse> availableWarehouses = warehouseRepository.findWarehousesWithCapacity();
        if (availableWarehouses.isEmpty()) {
            log.warn("⚠️ Khong co kho nao con suc chua.");
            return new BaseResponseDTO<>(0, "Khong co kho nao con suc chua.", null);
        }

        int processedCount = 0;
        for (Order order : orders) {
            Optional<Warehouse> selectedWarehouse = findNearestWarehouse(order, availableWarehouses);
            if (selectedWarehouse.isPresent()) {
                allocateOrderToWarehouse(order, selectedWarehouse.get(), user);
                processedCount++;
            } else {
                log.warn("⚠️ Khong co kho phu hop cho don hang: {}", order.getOrderId());
            }
        }

        return new BaseResponseDTO<>(1, "Da dieu phoi thanh cong " + processedCount + " don hang.", null);
    }

    private void allocateOrderToWarehouse(Order order, Warehouse warehouse, User user) {
        log.info("🔄 Dang xu ly don hang {} cho kho {}", order.getOrderId(), warehouse.getName());

        if (warehouse.getCapacity() > 0) {
            warehouse.setCapacity(warehouse.getCapacity() - 1);
            warehouseRepository.save(warehouse);
        } else {
            log.warn("🚨 Kho {} da het suc chua, khong the luu don hang {}", warehouse.getName(), order.getOrderId());
            return;
        }

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setHistoryId(generateHistoryId());
        orderHistory.setPerformedBy(user);
        orderHistory.setPerformedAt(LocalDateTime.now());
        orderHistory.setOrder(order);
        orderHistory.setWarehouse(warehouse);
        orderHistory.setStatus(1);
        orderHistoryRepository.save(orderHistory);

        order.setStatus(1);
        order.setWarehouse(warehouse);
        order.setStoredAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("✅ Don hang {} da duoc luu vao kho {} (Con {} cho trong)", order.getOrderId(), warehouse.getName(), warehouse.getCapacity());
    }

    private Optional<Warehouse> findNearestWarehouse(Order order, List<Warehouse> warehouses) {
        return warehouses.stream()
                .filter(w -> w.getCapacity() > 0)
                .min((w1, w2) -> Double.compare(
                        DistanceCalculator.calculateDistance(
                                w1.getLatitude().doubleValue(), w1.getLongitude().doubleValue(),
                                order.getReceiver().getLatitude().doubleValue(), order.getReceiver().getLongitude().doubleValue()
                        ),
                        DistanceCalculator.calculateDistance(
                                w2.getLatitude().doubleValue(), w2.getLongitude().doubleValue(),
                                order.getReceiver().getLatitude().doubleValue(), order.getReceiver().getLongitude().doubleValue()
                        )
                ));
    }

    private synchronized String generateHistoryId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderHistoryRepository.countByDate(LocalDate.now()) + 1;
        return String.format("HIS-%s-%05d", datePart, count);
    }
}
