package com.hungnguyen.srs_warehouse.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hungnguyen.srs_warehouse.service.PasswordMigrationService;

@Configuration
public class MigrationRunner {

    @Bean
    CommandLineRunner run(PasswordMigrationService passwordMigrationService) {
        return args -> {
            passwordMigrationService.migratePasswords();
            System.out.println("✅ Mật khẩu người dùng đã được mã hóa thành công!");
        };
    }
}

