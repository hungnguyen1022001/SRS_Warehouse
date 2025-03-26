package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    // Using JpaSpecificationExecutor for dynamic queries
//    @Query("SELECT MAX(CAST(SUBSTRING(o.orderId, 10, 5) AS long)) FROM Order o WHERE o.orderId LIKE CONCAT('DH-', :datePart, '-%')")
//    Long findMaxOrderIdByDate(@Param("datePart") String datePart);
    List<Order> findTop100ByStatusOrderByCreatedAtAsc(Integer status);
    List<Order> findByOrderIdContaining(String orderId);

    List<Order> findByOrderIdIn(List<String> orderIds);

    @Query("SELECT o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE', o.storedAt), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.warehouse.warehouseId IN :warehouseIds " +
            "AND o.storedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE', o.storedAt) " +
            "ORDER BY o.warehouse.warehouseId, FUNCTION('DATE', o.storedAt)")
    List<Object[]> getOrderStatisticsByDay(List<String> warehouseIds, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m'), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.warehouse.warehouseId IN :warehouseIds " +
            "AND o.storedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m') " +
            "ORDER BY o.warehouse.warehouseId, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m')")
    List<Object[]> getOrderStatisticsByMonth(List<String> warehouseIds, LocalDateTime startDate, LocalDateTime endDate);
}