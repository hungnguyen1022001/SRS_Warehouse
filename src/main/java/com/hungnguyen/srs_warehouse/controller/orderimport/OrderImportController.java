package com.hungnguyen.srs_warehouse.controller.orderimport;
import com.hungnguyen.srs_warehouse.util.ExcelUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/api/public/import")
public class OrderImportController {

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        return ExcelUtils.downloadTemplate();
    }
}

