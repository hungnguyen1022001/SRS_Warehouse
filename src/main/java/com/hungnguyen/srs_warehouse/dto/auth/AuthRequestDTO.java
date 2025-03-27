package com.hungnguyen.srs_warehouse.dto.auth;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String username;
    private String password;
    private String warehouseId;
}
