package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.service.OrderReportService;
import com.hungnguyen.srs_warehouse.util.report.ExcelReportUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReportServiceImpl implements OrderReportService {
    private final OrderRepository orderRepository;

    @Override
    public ResponseEntity<byte[]> generateDailyOrderReport(List<String> warehouseIds, String fromDate, String toDate) {
        return generateOrderReport(warehouseIds, fromDate, toDate, "day");
    }

    @Override
    public ResponseEntity<byte[]> generateMonthlyOrderReport(List<String> warehouseIds, String fromDate, String toDate) {
        return generateOrderReport(warehouseIds, fromDate, toDate, "month");
    }

    private ResponseEntity<byte[]> generateOrderReport(List<String> warehouseIds, String fromDate, String toDate, String periodType) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(fromDate + " 00:00:00", formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(toDate + " 23:59:59", formatter);

            List<String> dateHeaders;
            byte[] excelFile;
            String fileName;

            if ("month".equalsIgnoreCase(periodType)) {
                dateHeaders = getMonthRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByMonthDTO> reportData = orderRepository.getOrderReportByMonth(warehouseIds, startDateTime, endDateTime);
                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
            } else {
                dateHeaders = getDateRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByDayDTO> reportData = orderRepository.getOrderReportByDay(warehouseIds, startDateTime, endDateTime);
                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
            }

            fileName = ExcelReportUtils.generateFileName();
            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            Path filePath = Paths.get(desktopPath + fileName);
            Files.write(filePath, excelFile, StandardOpenOption.CREATE);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(("SERVER_ERROR: " + e.getMessage()).getBytes());
        }
    }

    private List<String> getDateRange(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .toList();
    }

    private List<String> getMonthRange(LocalDate start, LocalDate end) {
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        return startMonth.atDay(1).datesUntil(endMonth.atEndOfMonth().plusDays(1))
                .map(date -> YearMonth.from(date).format(DateTimeFormatter.ofPattern("MM/yyyy")))
                .distinct()
                .toList();
    }
}
