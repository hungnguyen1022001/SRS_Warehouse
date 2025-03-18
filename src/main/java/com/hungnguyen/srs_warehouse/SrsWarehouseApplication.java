package com.hungnguyen.srs_warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.hungnguyen.srs_warehouse")

public class SrsWarehouseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SrsWarehouseApplication.class, args);
    }
}
