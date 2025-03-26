package com.hungnguyen.srs_warehouse.controller.orderImport;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.service.OrderImport.ImportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller xử lý import đơn hàng từ file Excel
 */
@RestController
@RequestMapping("/api/import-orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    @PostMapping
    public ResponseEntity<BaseResponseDTO<?>> importOrdersFromExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new BaseResponseDTO<>(0, "EXCEL_FILE_NOT_FOUND", null));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        BaseResponseDTO<?> result = importOrderService.importOrders(file, username);
        return ResponseEntity.status(result.getStatus() == 1 ? 200 : 400).body(result);
    }
}
