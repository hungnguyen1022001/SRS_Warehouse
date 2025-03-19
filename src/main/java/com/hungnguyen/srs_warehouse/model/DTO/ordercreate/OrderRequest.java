    package com.hungnguyen.srs_warehouse.model.DTO.ordercreate;

    import jakarta.validation.constraints.*;

    public record OrderRequest(
            SupplierRequest supplier,
            ReceiverRequest receiver
    ) {}



