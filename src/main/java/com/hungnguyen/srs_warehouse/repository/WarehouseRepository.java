package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {
    @Query("SELECT w FROM Warehouse w WHERE w.capacity > 0")
    List<Warehouse> findWarehousesWithCapacity();

    @Query("SELECT w.warehouseId FROM Warehouse w")
    List<String> findAllWarehouseIds();
}

