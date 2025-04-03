package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.mapper.OrderLabelMapper;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.service.OrderLabelService;
import com.hungnguyen.srs_warehouse.util.label.ExcelLabelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLabelServiceImpl implements OrderLabelService {
    private final OrderRepository orderRepository;
    private final OrderLabelMapper orderLabelMapper;

    @Override
    public ResponseEntity<byte[]> generateLabelExcel(List<String> orderIds) {
        if (orderIds.size() > 10) {
            throw new CustomExceptions.FileGenerationException("EXCEL_MAX_ORDERS");
        }

        List<OrderLabelDTO> orders = orderRepository.findByOrderIdIn(orderIds)
                .stream()
                .map(orderLabelMapper::toDto)
                .toList();

        if (orders.size() != orderIds.size()) {
            throw new CustomExceptions.NotFoundException("ORDER_001");
        }

        byte[] excelFile = ExcelLabelUtils.createLabelFile(orders);
        String fileName = generateFileName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    private String generateFileName() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "Labels_" + date + ".xlsx";
    }
}
