package com.hungnguyen.srs_warehouse.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderLabelService {
    ResponseEntity<byte[]> generateLabelExcel(List<String> orderIds);
}
