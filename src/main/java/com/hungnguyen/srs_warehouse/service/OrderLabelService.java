package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.DTO.orderDetail.OrderLabelDTO;
import com.hungnguyen.srs_warehouse.mapper.OrderLabelMapper;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.util.label.ExcelLabelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderLabelService {
    private final OrderRepository orderRepository;
    private final OrderLabelMapper orderLabelMapper;
    private final MessageSource messageSource;

    public ResponseEntity<byte[]> generateLabelExcel(List<String> orderIds) {
        try {
            if (orderIds.size() > 10) {
                String message = messageSource.getMessage("EXCEL_MAX_ORDERS", null, Locale.getDefault());
                return ResponseEntity.badRequest()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .body(message.getBytes());
            }

            List<OrderLabelDTO> orders = orderRepository.findByOrderIdIn(orderIds)
                    .stream()
                    .map(orderLabelMapper::toDto)
                    .toList();

            if (orders.size() != orderIds.size()) {
                String message = messageSource.getMessage("ORDER_001", null, Locale.getDefault());
                return ResponseEntity.badRequest()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .body(message.getBytes());
            }

            byte[] excelFile = ExcelLabelUtils.createLabelFile(orders);
            String fileName = generateFileName();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("SERVER_ERROR", null, Locale.getDefault()) + ": " + e.getMessage();
            return ResponseEntity.internalServerError()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(errorMessage.getBytes());
        }
    }

    private String generateFileName() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "Labels_" + date + ".xlsx";
    }
}
