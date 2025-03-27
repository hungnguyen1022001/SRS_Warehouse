package com.hungnguyen.srs_warehouse.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDTO {
    private int status;
    private String message;
    private String accessToken;
    private String refreshToken;
    private String userId;
    private String username;
    private String warehouseId;
}
