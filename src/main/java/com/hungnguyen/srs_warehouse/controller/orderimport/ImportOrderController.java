package com.hungnguyen.srs_warehouse.controller.orderimport;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.orderimport.ImportOrderService;
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
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        if (file == null || file.isEmpty()) {
            BaseResponseDTO<?> response = new BaseResponseDTO<>(0, "EXCEL_FILE_NOT_FOUND ", null);
            return ResponseEntity.badRequest().body(response);
        }

        String jwtToken = token.replace("Bearer ", "");
        BaseResponseDTO<?> result = importOrderService.importOrders(file, jwtToken);

        return ResponseEntity.status(result.getStatus() == 1 ? 200 : 400).body(result);
    }
}