    package com.hungnguyen.srs_warehouse.dto.orderCreate;

    public record OrderRequest(
            SupplierRequest supplier,
            ReceiverRequest receiver
    ) {}



