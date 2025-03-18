package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "WAREHOUSE_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Warehouse {

    @Id
    @Column(name = "WAREHOUSE_ID", length = 25, nullable = false)
    private String warehouseId;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "ADDRESS", length = 200)
    private String address;

    @Column(name = "LATITUDE", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "CAPACITY")
    private Integer capacity;
}
