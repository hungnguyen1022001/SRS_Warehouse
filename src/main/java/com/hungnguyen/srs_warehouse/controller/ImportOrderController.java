package com.hungnguyen.srs_warehouse.controller;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.ImportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import-orders")
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    @Autowired
    public ImportOrderController(ImportOrderService importOrderService) {
        this.importOrderService = importOrderService;
    }

    @PostMapping
    public ResponseEntity<BaseResponseDTO<?>> importOrdersFromExcel(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(importOrderService.importOrders(file));
    }
}
