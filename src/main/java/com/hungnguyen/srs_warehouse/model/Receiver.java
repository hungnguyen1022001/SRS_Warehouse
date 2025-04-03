package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "RECEIVERS_TBL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Receiver {

    @Id
    @Column(name = "RECEIVER_ID", length = 20, nullable = false)
    private String receiverId;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "PHONE", length = 15)
    private String phone;

    @Column(name = "ADDRESS", length = 200)
    private String address;

    @Column(name = "LATITUDE", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "LONGITUDE", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "EMAIL", length = 255)
    private String email;
}
