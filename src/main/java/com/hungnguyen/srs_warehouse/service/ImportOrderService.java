package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;



public interface ImportOrderService {
    ResponseEntity<?> importOrders(MultipartFile file,HttpServletRequest request);
}
