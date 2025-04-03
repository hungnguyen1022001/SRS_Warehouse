package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_history_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class OrderHistory {

    @Id
    @Column(name = "history_id", length = 36, nullable = false, updatable = false)
    private String historyId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @CreationTimestamp
    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
}
