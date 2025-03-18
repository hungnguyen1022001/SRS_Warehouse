package com.hungnguyen.srs_warehouse.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCounter {
    @Id
    private String date; // "yyyy-MM-dd"
    private int counter;
}

