package com.hungnguyen.srs_warehouse.dto.orderCreate;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SupplierRequest(
        String name,
        String address,
        String phone,
        String email,
        BigDecimal latitude,
        BigDecimal longitude
) {}
