package com.hungnguyen.srs_warehouse.dto.orderCreate;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ReceiverRequest(
         String name,
         String address,
        String phone,
         String email,
        BigDecimal latitude,
         BigDecimal longitude
) {}
