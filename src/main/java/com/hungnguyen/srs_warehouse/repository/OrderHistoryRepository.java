package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
@Repository
public interface OrderHistoryRepository extends JpaRepository <OrderHistory, String> {
    @Query("SELECT COUNT(h) FROM OrderHistory h WHERE FUNCTION('DATE', h.performedAt) = :date")
    long countByDate(LocalDate date);


}
