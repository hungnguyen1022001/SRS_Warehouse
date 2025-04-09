package com.hungnguyen.srs_warehouse.service.impl;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.repository.WarehouseRepository;
import com.hungnguyen.srs_warehouse.service.OrderReportService;
import com.hungnguyen.srs_warehouse.util.report.ExcelReportUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReportServiceImpl implements OrderReportService {
    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DAY_PERIOD = "day";
    private static final String MONTH_PERIOD = "month";

    @Override
    public ResponseEntity<byte[]> generateDailyOrderReport(List<String> warehouseIds, String fromDate, String toDate) {
        return generateOrderReport(warehouseIds, fromDate, toDate, DAY_PERIOD);
    }

    @Override
    public ResponseEntity<byte[]> generateMonthlyOrderReport(List<String> warehouseIds, String fromDate, String toDate) {
        return generateOrderReport(warehouseIds, fromDate, toDate, MONTH_PERIOD);
    }

    private ResponseEntity<byte[]> generateOrderReport(List<String> warehouseIds, String fromDate, String toDate, String periodType) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(fromDate + " 00:00:00", DATE_FORMATTER);
            LocalDateTime endDateTime = LocalDateTime.parse(toDate + " 23:59:59", DATE_FORMATTER);

            List<Object[]> warehouseInfo = warehouseRepository.findIdAndNameByWarehouseIds(warehouseIds);
            Map<String, String> warehouseMap = createWarehouseMap(warehouseInfo);

            byte[] excelFile;
            List<String> dateHeaders;

            if (MONTH_PERIOD.equalsIgnoreCase(periodType)) {
                dateHeaders = getMonthRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByMonthDTO> reportData = orderRepository.getOrderReportByMonth(warehouseIds, startDateTime, endDateTime);
                reportData = ensureAllWarehousesInMonthlyReport(reportData, warehouseMap, dateHeaders);
                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
            } else {
                dateHeaders = getDateRange(startDateTime.toLocalDate(), endDateTime.toLocalDate());
                List<OrderReportByDayDTO> reportData = orderRepository.getOrderReportByDay(warehouseIds, startDateTime, endDateTime);
                reportData = ensureAllWarehousesInDailyReport(reportData, warehouseMap, dateHeaders);
                excelFile = ExcelReportUtils.createOrderReportFile(reportData, dateHeaders);
            }

            String fileName = ExcelReportUtils.generateFileName();
            return createDownloadResponse(fileName, excelFile);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(("SERVER_ERROR: " + e.getMessage()).getBytes());
        }
    }

    private Map<String, String> createWarehouseMap(List<Object[]> warehouseInfo) {
        Map<String, String> warehouseMap = new HashMap<>();
        for (Object[] warehouse : warehouseInfo) {
            warehouseMap.put((String) warehouse[0], (String) warehouse[1]);
        }
        return warehouseMap;
    }

    private List<OrderReportByDayDTO> ensureAllWarehousesInDailyReport(
            List<OrderReportByDayDTO> reportData,
            Map<String, String> warehouseMap,
            List<String> dateHeaders) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime firstDate = LocalDate.parse(dateHeaders.get(0), formatter).atStartOfDay();

        Map<String, Boolean> existingWarehouseMap = reportData.stream()
                .collect(Collectors.toMap(
                        OrderReportByDayDTO::getWarehouseId,
                        dto -> true,
                        (existing, replacement) -> existing
                ));

        warehouseMap.forEach((warehouseId, warehouseName) -> {
            if (!existingWarehouseMap.containsKey(warehouseId)) {
                reportData.add(new OrderReportByDayDTO(
                        warehouseId,
                        warehouseName,
                        firstDate,
                        0L
                ));
            }
        });

        return reportData;
    }

    private List<OrderReportByMonthDTO> ensureAllWarehousesInMonthlyReport(
            List<OrderReportByMonthDTO> reportData,
            Map<String, String> warehouseMap,
            List<String> dateHeaders) {

        Map<String, Boolean> existingWarehouseMap = reportData.stream()
                .collect(Collectors.toMap(
                        OrderReportByMonthDTO::getWarehouseId,
                        dto -> true,
                        (existing, replacement) -> existing
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        YearMonth firstMonth = YearMonth.parse(dateHeaders.get(0), formatter);

        warehouseMap.forEach((warehouseId, warehouseName) -> {
            if (!existingWarehouseMap.containsKey(warehouseId)) {
                reportData.add(new OrderReportByMonthDTO(
                        warehouseId,
                        warehouseName,
                        firstMonth.getYear(),
                        firstMonth.getMonthValue(),
                        0L
                ));
            }
        });

        return reportData;
    }

    private ResponseEntity<byte[]> createDownloadResponse(String fileName, byte[] content) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
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
