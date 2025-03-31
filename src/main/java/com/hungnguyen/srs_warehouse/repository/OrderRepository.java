package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;

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

    @Query("SELECT new com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO(" +
           "w.warehouseId, w.name, o.storedAt, COUNT(o.orderId)) " +
           "FROM Order o " +
           "JOIN o.warehouse w " +
           "WHERE w.warehouseId IN :warehouseIds " +
           "AND o.storedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY w.warehouseId, w.name, o.storedAt " +
           "ORDER BY w.warehouseId, o.storedAt")
    List<OrderReportByDayDTO> getOrderReportByDay(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    /**
     * Báo cáo đơn hàng theo tháng (sử dụng YearMonth)
     */
    @Query("SELECT new com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO(" +
           "w.warehouseId, w.name, EXTRACT(YEAR FROM o.storedAt), EXTRACT(MONTH FROM o.storedAt), COUNT(o.id)) " +
           "FROM Order o " +
           "JOIN o.warehouse w " +
           "WHERE w.warehouseId IN :warehouseIds " +
           "AND o.storedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY w.warehouseId, w.name, EXTRACT(YEAR FROM o.storedAt), EXTRACT(MONTH FROM o.storedAt) " +
           "ORDER BY w.warehouseId, EXTRACT(YEAR FROM o.storedAt), EXTRACT(MONTH FROM o.storedAt)")
    List<OrderReportByMonthDTO> getOrderReportByMonth(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
