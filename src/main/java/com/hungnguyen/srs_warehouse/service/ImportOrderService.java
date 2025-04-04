package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;



public interface ImportOrderService {
    BaseResponseDTO<?> importOrders(MultipartFile file,HttpServletRequest request);
}
