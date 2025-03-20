package com.hungnguyen.srs_warehouse.controller.orderimport;

import com.hungnguyen.srs_warehouse.service.orderimport.ImportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/import-orders")
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    @Autowired
    public ImportOrderController(ImportOrderService importOrderService) {
        this.importOrderService = importOrderService;
    }

    @PostMapping()
    public ResponseEntity<Map<String, Object>> importOrdersFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("status", 0, "message", "Không tìm thấy file upload hoặc file trống!"));
        }

        String jwtToken = token.replace("Bearer ", "");
        Map<String, Object> result = importOrderService.importOrders(file, jwtToken);
        HttpStatus status = (int) result.get("status") == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(result, status);
    }
}
