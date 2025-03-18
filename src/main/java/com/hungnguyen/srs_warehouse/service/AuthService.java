package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.DTO.AuthRequestDTO;
import com.hungnguyen.srs_warehouse.model.DTO.AuthResponseDTO;
import com.hungnguyen.srs_warehouse.model.User;
import com.hungnguyen.srs_warehouse.repository.UserRepository;
import com.hungnguyen.srs_warehouse.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        Optional<User> userOpt = userRepository.findByUsernameAndWarehouse_WarehouseId(request.getUsername(), request.getWarehouseId());

        if (userOpt.isEmpty()) {
            return new AuthResponseDTO(0, getMessage("USER_001"), null, null, null, null, null);
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponseDTO(0, getMessage("USER_001"), null, null, null, null, null);
        }

        String accessToken = jwtUtils.generateToken(request.getUsername(), request.getWarehouseId(), false);
        String refreshToken = jwtUtils.generateToken(request.getUsername(), request.getWarehouseId(), true);

        return new AuthResponseDTO(
                1,
                getMessage("SUCCESS"),
                accessToken,
                refreshToken,
                user.getUserId(),
                user.getUsername(),
                user.getWarehouse().getWarehouseId()
        );
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
