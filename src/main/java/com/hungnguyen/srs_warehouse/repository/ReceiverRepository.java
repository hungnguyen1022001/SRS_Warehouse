package com.hungnguyen.srs_warehouse.repository;

import com.hungnguyen.srs_warehouse.model.Receiver;
import com.hungnguyen.srs_warehouse.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, String> {
    Optional<Receiver> findByNameAndPhone(String name, String phone);
    Optional<Receiver> findByPhone(String phone);
}
