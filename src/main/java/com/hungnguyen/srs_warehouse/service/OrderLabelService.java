package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.config.MessageConfig;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import com.hungnguyen.srs_warehouse.mapper.OrderLabelMapper;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
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
public class OrderLabelService {
    private final OrderRepository orderRepository;
    private final OrderLabelMapper orderLabelMapper;

    public ResponseEntity<byte[]> generateLabelExcel(List<String> orderIds) {
        try {
            if (orderIds.size() > 10) {
                return badRequestResponse("EXCEL_MAX_ORDERS");
            }

            List<OrderLabelDTO> orders = orderRepository.findByOrderIdIn(orderIds)
                    .stream()
                    .map(orderLabelMapper::toDto)
                    .toList();

            if (orders.size() != orderIds.size()) {
                return badRequestResponse("ORDER_001");
            }

            byte[] excelFile = ExcelLabelUtils.createLabelFile(orders);
            String fileName = generateFileName();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (Exception e) {
            return internalServerErrorResponse("SERVER_ERROR", e);
        }
    }

    private String generateFileName() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "Labels_" + date + ".xlsx";
    }

    private ResponseEntity<byte[]> badRequestResponse(String messageKey) {
        String message = MessageConfig.getMessage(messageKey);
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(message.getBytes());
    }

    private ResponseEntity<byte[]> internalServerErrorResponse(String messageKey, Exception e) {
        String errorMessage = MessageConfig.getMessage(messageKey) + ": " + e.getMessage();
        return ResponseEntity.internalServerError()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(errorMessage.getBytes());
    }
}
