    package com.hungnguyen.srs_warehouse.model.DTO.orderCreate;

    public record OrderRequest(
            SupplierRequest supplier,
            ReceiverRequest receiver
    ) {}



