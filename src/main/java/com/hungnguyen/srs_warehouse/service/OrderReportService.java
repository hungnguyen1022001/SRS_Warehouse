package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.util.report.ExcelReportUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j // ✅ Kích hoạt SLF4J logging
public class OrderReportService {
    private final OrderRepository orderRepository;

    public ResponseEntity<byte[]> generateDailyOrderReport(List<String> warehouseIds, String fromDate, String toDate) {
        return generateOrderReport(warehouseIds, fromDate, toDate, "day");
    }

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

            log.info("📊 Generating {} report from {} to {} for warehouses: {}", periodType, startDateTime, endDateTime, warehouseIds);

            if ("month".equalsIgnoreCase(periodType)) {
                dateHeaders = getMonthRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByMonthDTO> reportData = orderRepository.getOrderReportByMonth(warehouseIds, startDateTime, endDateTime);

                log.info("✅ Query executed successfully. Retrieved {} records.", reportData.size());
                log.debug("📄 Data: {}", reportData); // Debug log để xem dữ liệu chi tiết

                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
                fileName = ExcelReportUtils.generateFileName();
            } else {
                dateHeaders = getDateRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByDayDTO> reportData = orderRepository.getOrderReportByDay(warehouseIds, startDateTime, endDateTime);

                log.info("✅ Query executed successfully. Retrieved {} records.", reportData.size());
                log.debug("📄 Data: {}", reportData); // Debug log để xem dữ liệu chi tiết

                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
                fileName = ExcelReportUtils.generateFileName();
            }

            String desktopPath = System.getProperty("user.home") + "/Desktop/";
            Path filePath = Paths.get(desktopPath + fileName);
            Files.write(filePath, excelFile, StandardOpenOption.CREATE);

            log.info("📂 Report file saved: {}", filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);
        } catch (IOException e) {
            log.error("❌ Error generating report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(("SERVER_ERROR: " + e.getMessage()).getBytes());
        }
    }

    private List<String> getDateRange(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))  // Format theo `dd/MM/yyyy`
                .toList();
    }


    private List<String> getMonthRange(LocalDate start, LocalDate end) {
        YearMonth startMonth = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        return startMonth.atDay(1).datesUntil(endMonth.atEndOfMonth().plusDays(1))
                .map(date -> YearMonth.from(date).format(DateTimeFormatter.ofPattern("MM/yyyy"))) // Format theo `MM/yyyy`
                .distinct()
                .toList();
    }

}
