package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
}

