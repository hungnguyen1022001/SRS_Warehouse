package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findByUsernameAndWarehouse_WarehouseId(String username, String warehouseId);


    Optional<User> findByUsername(String username);
}
