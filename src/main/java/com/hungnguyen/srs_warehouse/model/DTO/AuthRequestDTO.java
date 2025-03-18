package com.hungnguyen.srs_warehouse.model.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String username;
    private String password;
    private String warehouseId;
}
