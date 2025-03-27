package com.hungnguyen.srs_warehouse.dto.orderCreate;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ReceiverRequest(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 200) String address,
        @NotBlank @Pattern(regexp = "\\d{1,11}") String phone,
        @Email @Size(max = 50) String email,
        @NotNull @DecimalMin("-90.000000") @DecimalMax("90.000000") BigDecimal latitude,
        @NotNull @DecimalMin("-180.000000") @DecimalMax("180.000000") BigDecimal longitude
) {}
