package com.hungnguyen.srs_warehouse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USERS_TBL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
@EqualsAndHashCode
public class User {

    @Id
    @Column(name = "USER_ID", length = 10, nullable = false)
    private String userId;

    @Column(name = "USERNAME", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID", nullable = false)
    private Warehouse warehouse;
}
