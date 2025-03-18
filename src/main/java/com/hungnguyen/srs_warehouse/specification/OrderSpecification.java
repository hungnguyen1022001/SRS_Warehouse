package com.hungnguyen.srs_warehouse.specification;

import com.hungnguyen.srs_warehouse.model.DTO.OrderSearchCriteria;
import com.hungnguyen.srs_warehouse.model.Order;
import com.hungnguyen.srs_warehouse.model.Receiver;
import com.hungnguyen.srs_warehouse.model.Supplier;
import com.hungnguyen.srs_warehouse.model.Warehouse;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> withSearchCriteria(OrderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Order ID search (contains)
            if (criteria.getOrderId() != null && !criteria.getOrderId().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("orderId")),
                        "%" + criteria.getOrderId().toLowerCase() + "%"
                ));
            }

            // Phone search (contains) - search in both supplier and receiver
            if (criteria.getPhone() != null && !criteria.getPhone().isEmpty()) {
                Join<Order, Supplier> supplierJoin = root.join("supplier");
                Join<Order, Receiver> receiverJoin = root.join("receiver");

                Predicate supplierPhonePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(supplierJoin.get("phone")),
                        "%" + criteria.getPhone().toLowerCase() + "%"
                );

                Predicate receiverPhonePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(receiverJoin.get("phone")),
                        "%" + criteria.getPhone().toLowerCase() + "%"
                );

                predicates.add(criteriaBuilder.or(supplierPhonePredicate, receiverPhonePredicate));
            }

            // Status filter
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // Warehouse filter
            if (criteria.getWarehouseIds() != null && !criteria.getWarehouseIds().isEmpty()) {
                Join<Order, Warehouse> warehouseJoin = root.join("warehouse");
                predicates.add(warehouseJoin.get("warehouseId").in(criteria.getWarehouseIds()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
