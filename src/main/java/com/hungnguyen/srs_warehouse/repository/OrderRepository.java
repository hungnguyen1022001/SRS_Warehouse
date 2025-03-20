package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    // Using JpaSpecificationExecutor for dynamic queries
    @Query("SELECT MAX(CAST(SUBSTRING(o.orderId, 10, 5) AS long)) FROM Order o WHERE o.orderId LIKE CONCAT('DH-', :datePart, '-%')")
    Long findMaxOrderIdByDate(@Param("datePart") String datePart);
}