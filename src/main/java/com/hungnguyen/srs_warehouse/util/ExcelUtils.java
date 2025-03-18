package com.hungnguyen.srs_warehouse.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.io.IOException;

public class ExcelUtils {

    public static ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource file = new ClassPathResource("templates/INB_ImportData.xlsx");
        if (!file.exists()) {
            throw new IOException("File không tồn tại: " + file.getFilename());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=INB_ImportData.xlsx")
                .body(file);
    }
}

