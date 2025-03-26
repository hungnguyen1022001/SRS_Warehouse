package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Repository xử lý truy vấn dữ liệu cho bảng Order
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    /**
     * Lấy danh sách 100 đơn hàng có trạng thái nhất định, sắp xếp theo ngày tạo tăng dần
     */
    List<Order> findTop100ByStatusOrderByCreatedAtAsc(Integer status);

    /**
     * Tìm danh sách đơn hàng theo mã đơn hàng
     */
    List<Order> findByOrderIdContaining(String orderId);

    /**
     * Lấy danh sách đơn hàng theo danh sách mã đơn hàng
     */
    List<Order> findByOrderIdIn(List<String> orderIds);

    /**
     * Thống kê số lượng đơn hàng theo ngày cho các kho hàng được chọn
     */
    @Query("""
        SELECT o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE', o.storedAt), COUNT(o)
        FROM Order o
        WHERE o.warehouse.warehouseId IN :warehouseIds
        AND o.storedAt BETWEEN :startDate AND :endDate
        GROUP BY o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE', o.storedAt)
        ORDER BY o.warehouse.warehouseId, FUNCTION('DATE', o.storedAt)
    """)
    List<Object[]> getOrderStatisticsByDay(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Thống kê số lượng đơn hàng theo tháng cho các kho hàng được chọn
     */
    @Query("""
        SELECT o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m'), COUNT(o)
        FROM Order o
        WHERE o.warehouse.warehouseId IN :warehouseIds
        AND o.storedAt BETWEEN :startDate AND :endDate
        GROUP BY o.warehouse.warehouseId, o.warehouse.name, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m')
        ORDER BY o.warehouse.warehouseId, FUNCTION('DATE_FORMAT', o.storedAt, '%Y-%m')
    """)
    List<Object[]> getOrderStatisticsByMonth(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
