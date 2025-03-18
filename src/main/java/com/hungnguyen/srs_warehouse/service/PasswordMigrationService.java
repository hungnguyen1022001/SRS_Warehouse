package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.User;
import com.hungnguyen.srs_warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PasswordMigrationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void migratePasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // Kiểm tra nếu mật khẩu chưa mã hóa
            if (!user.getPassword().startsWith("$2a$")) { // BCrypt hash luôn bắt đầu với "$2a$"
                String plainPassword = user.getPassword();
                String encodedPassword = passwordEncoder.encode(plainPassword);
                user.setPassword(encodedPassword);
            }
        }
        userRepository.saveAll(users);
    }
}
