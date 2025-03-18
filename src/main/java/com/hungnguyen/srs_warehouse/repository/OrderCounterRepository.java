package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.OrderCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCounterRepository extends JpaRepository<OrderCounter, String>, JpaSpecificationExecutor<OrderCounter> {
    // Using JpaSpecificationExecutor for dynamic queries

}
