package com.hungnguyen.srs_warehouse.dto.orderDispatch;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderDTO {
    private String orderId;
    private LocalDateTime createdAt;
    private int status;
    private LocalDateTime storedAt;

    public OrderDTO() {}

    public OrderDTO(String orderId, LocalDateTime createdAt, int status, LocalDateTime storedAt) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.status = status;
        this.storedAt = storedAt;
    }
}
