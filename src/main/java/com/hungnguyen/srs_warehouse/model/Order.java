package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Order {

    @Id
    @Column(name = "ORDER_ID", length = 20, nullable = false)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_ID")
    private Receiver receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID")
    private Warehouse warehouse;

    @Column(name = "CREATED_BY", length = 20, nullable = false)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATE_AT")
    private LocalDateTime updatedAt;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "STORED_AT")
    private LocalDateTime storedAt;

    @Column(name = "DELIVERED_AT")
    private LocalDateTime deliveredAt;

    @Column(name = "FAILED_DELIVERIES", columnDefinition = "int default 0")
    private Integer failedDeliveries = 0;

    @Column(name = "RETURN_AT")
    private LocalDateTime returnAt;
}
