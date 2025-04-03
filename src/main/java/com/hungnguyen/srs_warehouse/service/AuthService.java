package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.dto.auth.AuthRequestDTO;
import com.hungnguyen.srs_warehouse.dto.auth.AuthResponseDTO;

public interface AuthService {
    BaseResponseDTO<AuthResponseDTO> authenticate(AuthRequestDTO request);
}