package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.AuthService;
import com.hungnguyen.srs_warehouse.dto.auth.AuthRequestDTO;
import com.hungnguyen.srs_warehouse.dto.auth.AuthResponseDTO;
import com.hungnguyen.srs_warehouse.model.User;
import com.hungnguyen.srs_warehouse.repository.UserRepository;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public BaseResponseDTO<AuthResponseDTO> authenticate(AuthRequestDTO request) {
        // Find user by username and warehouse ID
        Optional<User> userOpt = userRepository.findByUsernameAndWarehouse_WarehouseId(request.getUsername(), request.getWarehouseId());

        // Throw NotFoundException if user is not found
        if (userOpt.isEmpty()) {
            throw new CustomExceptions.NotFoundException("USER_001");
        }

        User user = userOpt.get();

        // Throw InvalidCredentialsException if password does not match
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomExceptions.NotFoundException("USER_001");
        }

        // Generate tokens
        String accessToken = jwtUtils.generateToken(request.getUsername(), request.getWarehouseId(), false);
        String refreshToken = jwtUtils.generateToken(request.getUsername(), request.getWarehouseId(), true);

        // Build and return response DTO
        AuthResponseDTO authData = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .username(user.getUsername())
                .warehouseId(user.getWarehouse().getWarehouseId())
                .build();

        return BaseResponseDTO.success("SUCCESS", authData);
    }
}
