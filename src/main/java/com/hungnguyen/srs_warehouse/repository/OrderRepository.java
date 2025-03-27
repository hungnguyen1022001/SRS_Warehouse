package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

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




}
