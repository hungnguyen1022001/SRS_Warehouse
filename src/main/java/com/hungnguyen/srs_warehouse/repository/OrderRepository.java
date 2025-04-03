package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.constants.OrderQueryConstants;
import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    List<Order> findTop100ByStatusOrderByCreatedAtAsc(@Param("status") Integer status);

    List<Order> findByOrderIdContaining(@Param("orderId") String orderId);

    List<Order> findByOrderIdIn(@Param("orderIds") List<String> orderIds);

    @Query(OrderQueryConstants.SEARCH_ORDERS)
    Page<Order> searchOrders(
            @Param("orderId") String orderId,
            @Param("phone") String phone,
            @Param("status") Integer status,
            @Param("warehouseId") String warehouseId,
            Pageable pageable
    );

    @Query(OrderQueryConstants.ORDER_REPORT_BY_DAY)
    List<OrderReportByDayDTO> getOrderReportByDay(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(OrderQueryConstants.ORDER_REPORT_BY_MONTH)
    List<OrderReportByMonthDTO> getOrderReportByMonth(
            @Param("warehouseIds") List<String> warehouseIds,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(OrderQueryConstants.FIND_MAX_ORDER_ID_FOR_TODAY)
    String findMaxOrderIdForToday(@Param("datePart") String datePart);

}
