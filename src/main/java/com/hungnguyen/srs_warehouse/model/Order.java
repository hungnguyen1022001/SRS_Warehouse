package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import lombok.Builder;
@Entity
@Table(name = "ORDERS_TBL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Order {

    @Id
    @Column(name = "ORDER_ID", length = 20, nullable = false)
    private String orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUPPLIER_ID")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RECEIVER_ID")
    private Receiver receiver;

    @ManyToOne(fetch = FetchType.EAGER)
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

    @Column(name = "FAILED_DELIVERIES")
    private Integer failedDeliveries = 0;

    @Column(name = "RETURN_AT")
    private LocalDateTime returnAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderHistory> orderHistories;

    // ✅ Các phương thức getter bổ sung cho các thông tin cần thiết

    // Lấy ID của warehouse
    public String getWarehouseId() {
        return warehouse != null ? warehouse.getWarehouseId() : null;
    }

    // Lấy tên của warehouse
    public String getWarehouseName() {
        return warehouse != null ? warehouse.getName() : null;
    }

    // Lấy ID của supplier
    public String getSupplierId() {
        return supplier != null ? supplier.getSupplierId() : null;
    }

    // Lấy tên của supplier
    public String getSupplierName() {
        return supplier != null ? supplier.getName() : null;
    }

    // Lấy địa chỉ của supplier
    public String getSupplierAddress() {
        return supplier != null ? supplier.getAddress() : null;
    }

    // Lấy số điện thoại của supplier
    public String getSupplierPhone() {
        return supplier != null ? supplier.getPhone() : null;
    }

    // Lấy email của supplier
    public String getSupplierEmail() {
        return supplier != null ? supplier.getEmail() : null;
    }

    // Lấy ID của receiver
    public String getReceiverId() {
        return receiver != null ? receiver.getReceiverId() : null;
    }

    // Lấy tên của receiver
    public String getReceiverName() {
        return receiver != null ? receiver.getName() : null;
    }

    // Lấy địa chỉ của receiver
    public String getReceiverAddress() {
        return receiver != null ? receiver.getAddress() : null;
    }

    // Lấy số điện thoại của receiver
    public String getReceiverPhone() {
        return receiver != null ? receiver.getPhone() : null;
    }

    // Lấy email của receiver
    public String getReceiverEmail() {
        return receiver != null ? receiver.getEmail() : null;
    }
}
