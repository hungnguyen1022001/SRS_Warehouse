package com.hungnguyen.srs_warehouse.controller.orderimport;

import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.service.orderimport.ImportOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import-orders")
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    @Autowired
    public ImportOrderController(ImportOrderService importOrderService) {
        this.importOrderService = importOrderService;
    }

    @PostMapping("/excel")
    public ResponseEntity<Map<String, Object>> importOrdersFromExcel(@RequestParam(value = "file", required = false) MultipartFile file) {
        System.out.println("🔍 Kiểm tra request nhận được...");

        if (file == null) {
            System.out.println("❌ Không nhận được file nào trong request!");
            return ResponseEntity.badRequest().body(Map.of("status", 0, "message", "Không tìm thấy file upload!"));
        }

        if (file.isEmpty()) {
            System.out.println("❌ File rỗng!");
            return ResponseEntity.badRequest().body(Map.of("status", 0, "message", "File trống!"));
        }

        System.out.println("✅ Đã nhận file: " + file.getOriginalFilename());
        System.out.println("📏 Kích thước file: " + file.getSize() + " bytes");

        Map<String, Object> result = importOrderService.importOrders(file);
        HttpStatus status = (int) result.get("status") == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(result, status);
    }


}
