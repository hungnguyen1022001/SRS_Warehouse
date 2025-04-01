package com.hungnguyen.srs_warehouse.util.report;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.apache.poi.ss.usermodel.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelReportFillUitls {

    public static void clearOldData(Sheet sheet) {
        for (int rowIndex = 2; rowIndex <= 12; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) sheet.removeRow(row);
        }
    }

    public static void writeReportHeader(Sheet sheet, List<String> dateHeaders) {
        Row headerRow = sheet.getRow(2);
        if (headerRow != null) {
            int colIndex = 3;
            for (String date : dateHeaders) {
                headerRow.createCell(colIndex++).setCellValue(date);
            }
        }
    }

    public static void writeReportData(Sheet sheet, List<?> reportData, List<String> dateHeaders, Workbook workbook) {
        if (reportData.isEmpty()) return;

        if (reportData.get(0) instanceof OrderReportByDayDTO) {
            writeDailyReport(sheet, (List<OrderReportByDayDTO>) reportData, dateHeaders, workbook);
        } else if (reportData.get(0) instanceof OrderReportByMonthDTO) {
            writeMonthlyReport(sheet, (List<OrderReportByMonthDTO>) reportData, dateHeaders, workbook);
        }
    }

    private static void writeDailyReport(Sheet sheet, List<OrderReportByDayDTO> data, List<String> headers, Workbook workbook) {
        Map<String, List<OrderReportByDayDTO>> groupedData = data.stream()
                .collect(Collectors.groupingBy(OrderReportByDayDTO::getWarehouseId));

        CellStyle style = ExcelStyleUtils.createCellStyle(workbook);
        int rowIndex = 3, stt = 1;

        for (var entry : groupedData.entrySet()) {
            Row row = sheet.createRow(rowIndex++);
            ExcelStyleUtils.createCell(row, 0, stt++, style);
            ExcelStyleUtils.createCell(row, 1, entry.getKey(), style);
            ExcelStyleUtils.createCell(row, 2, entry.getValue().get(0).getWarehouseName(), style);

            Map<String, Long> orderMap = entry.getValue().stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getStoredAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            Collectors.summingLong(OrderReportByDayDTO::getOrderCount)
                    ));

            int colIndex = 3;
            for (String date : headers) {
                ExcelStyleUtils.createCell(row, colIndex++, orderMap.getOrDefault(date, 0L), style);
            }
        }
    }

    private static void writeMonthlyReport(Sheet sheet, List<OrderReportByMonthDTO> data, List<String> headers, Workbook workbook) {
        Map<String, List<OrderReportByMonthDTO>> groupedData = data.stream()
                .collect(Collectors.groupingBy(OrderReportByMonthDTO::getWarehouseId));

        CellStyle style = ExcelStyleUtils.createCellStyle(workbook);
        int rowIndex = 3, stt = 1;

        for (var entry : groupedData.entrySet()) {
            Row row = sheet.createRow(rowIndex++);
            ExcelStyleUtils.createCell(row, 0, stt++, style);
            ExcelStyleUtils.createCell(row, 1, entry.getKey(), style);
            ExcelStyleUtils.createCell(row, 2, entry.getValue().get(0).getWarehouseName(), style);

            Map<String, Long> orderMap = entry.getValue().stream()
                    .collect(Collectors.toMap(
                            r -> r.getReportMonth().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                            OrderReportByMonthDTO::getOrderCount
                    ));

            int colIndex = 3;
            for (String date : headers) {
                ExcelStyleUtils.createCell(row, colIndex++, orderMap.getOrDefault(date, 0L), style);
            }
        }
    }
}
