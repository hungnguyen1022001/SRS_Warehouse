package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String> {
    Optional<Supplier> findByNameAndPhone(String name, String phone);
    Optional<Supplier> findByPhone(String phone);
}