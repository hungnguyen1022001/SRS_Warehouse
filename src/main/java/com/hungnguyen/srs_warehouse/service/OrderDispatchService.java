package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.*;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.repository.*;
import com.hungnguyen.srs_warehouse.util.DistanceCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDispatchService {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * Xử lý điều phối đơn hàng
     * @param username Người thực hiện điều phối
     * @param isBatchJob Nếu true, chạy dưới dạng batch
     * @return BaseResponseDTO<String> chứa thông tin kết quả điều phối
     */
    @Transactional
    public BaseResponseDTO<String> processOrders(String username, boolean isBatchJob) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("USER_001"));

            List<Order> orders = orderRepository.findTop100ByStatusOrderByCreatedAtAsc(0);
            if (orders.isEmpty()) {
                return new BaseResponseDTO<>(0, getMessage("ORDER_004"), null);
            }

            List<Warehouse> availableWarehouses = warehouseRepository.findWarehousesWithCapacity();
            if (availableWarehouses.isEmpty()) {
                return new BaseResponseDTO<>(0, getMessage("WAREHOUSE_001"), null);
            }

            int processedCount = allocateOrders(orders, availableWarehouses, user);
            return new BaseResponseDTO<>(1, getMessage("DISPATCH_001", processedCount), null);

        } catch (RuntimeException ex) {
            return new BaseResponseDTO<>(0, getMessage(ex.getMessage()), null);
        } catch (Exception ex) {
            return new BaseResponseDTO<>(0, getMessage("SERVER_ERROR"), null);
        }
    }

    /**
     * Phân bổ đơn hàng vào kho
     * @param orders Danh sách đơn hàng cần xử lý
     * @param warehouses Danh sách kho còn sức chứa
     * @param user Người thực hiện điều phối
     * @return Số lượng đơn hàng đã điều phối
     */
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

    /**
     * Phân bổ một đơn hàng vào kho
     * @param order Đơn hàng cần điều phối
     * @param warehouse Kho hàng được chọn
     * @param user Người thực hiện điều phối
     */
    private void allocateOrderToWarehouse(Order order, Warehouse warehouse, User user) {
        if (warehouse.getCapacity() > 0) {
            warehouse.setCapacity(warehouse.getCapacity() - 1);
            warehouseRepository.save(warehouse);
        } else {
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
    }

    /**
     * Tìm kho hàng gần nhất với người nhận
     * @param order Đơn hàng cần tìm kho
     * @param warehouses Danh sách kho còn sức chứa
     * @return Kho hàng gần nhất nếu tìm thấy
     */
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

    /**
     * Tạo ID lịch sử điều phối đơn hàng
     * @return Mã lịch sử định dạng HIS-yyyyMMdd-XXXXX
     */
    private synchronized String generateHistoryId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderHistoryRepository.countByDate(LocalDate.now()) + 1;
        return String.format("HIS-%s-%05d", datePart, count);
    }

    /**
     * Lấy message theo mã lỗi từ file messages.properties
     * @param code Mã lỗi
     * @param args Tham số động nếu có
     * @return Thông điệp tương ứng với mã lỗi
     */
    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
