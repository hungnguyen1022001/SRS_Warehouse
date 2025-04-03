package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.util.IdGeneratorUtil;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.service.OrderDispatchService;
import com.hungnguyen.srs_warehouse.util.DistanceCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDispatchServiceImpl implements OrderDispatchService {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    @Override
    @Transactional
    public BaseResponseDTO<String> processOrders(String username, boolean isBatchJob) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new CustomExceptions.NotFoundException("ORDER_001"));

            List<Order> orders = orderRepository.findTop100ByStatusOrderByCreatedAtAsc(0);
            if (orders.isEmpty()) {
                return BaseResponseDTO.fail("ORDER_001");
            }

            List<Warehouse> availableWarehouses = warehouseRepository.findWarehousesWithCapacity();
            if (availableWarehouses.isEmpty()) {
                throw new CustomExceptions.NotFoundException("WAREHOUSE_001");
            }

            int processedCount = allocateOrders(orders, availableWarehouses, user);
            return BaseResponseDTO.success("DISPATCH_001", String.valueOf(processedCount));

    }

    private int allocateOrders(List<Order> orders, List<Warehouse> warehouses, User user) {
        int processedCount = 0;
        for (Order order : orders) {
            Optional<Warehouse> selectedWarehouse = findNearestWarehouse(order, warehouses);
            if (selectedWarehouse.isPresent()) {
                allocateOrderToWarehouse(order, selectedWarehouse.get(), user);
                processedCount++;
            }
        }
        return processedCount;
    }

    private void allocateOrderToWarehouse(Order order, Warehouse warehouse, User user) {
        if (warehouse.getCapacity() > 0) {
            warehouse.setCapacity(warehouse.getCapacity() - 1);
            warehouseRepository.save(warehouse);
        } else {
            return;
        }
        OrderHistory orderHistory = OrderHistory.builder()
                .historyId(idGeneratorUtil.generateOrderHistoryId())
                .performedBy(user)
                .performedAt(LocalDateTime.now())
                .order(order)
                .warehouse(warehouse)
                .status(1)
                .build();
        orderHistoryRepository.save(orderHistory);

        order.setStatus(1);
        order.setWarehouse(warehouse);
        order.setStoredAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private Optional<Warehouse> findNearestWarehouse(Order order, List<Warehouse> warehouses) {
        double orderLat = order.getReceiver().getLatitude().doubleValue();
        double orderLon = order.getReceiver().getLongitude().doubleValue();

        return warehouses.stream()
                .filter(w -> w.getCapacity() > 0)
                .min((w1, w2) -> Double.compare(
                        DistanceCalculator.calculateDistance(w1.getLatitude().doubleValue(), w1.getLongitude().doubleValue(), orderLat, orderLon),
                        DistanceCalculator.calculateDistance(w2.getLatitude().doubleValue(), w2.getLongitude().doubleValue(), orderLat, orderLon)
                ));
    }

}
