package com.hungnguyen.srs_warehouse.util.report;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.apache.poi.ss.usermodel.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerators {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    public static void generateDailyReport(Sheet sheet, Workbook workbook,
                                           List<OrderReportByDayDTO> reportData,
                                           List<String> dateHeaders) {
        Map<String, List<OrderReportByDayDTO>> groupedData = reportData.stream()
                .collect(Collectors.groupingBy(OrderReportByDayDTO::getWarehouseId));

        CellStyle cellStyle = ExcelFormatUtils.createCellStyle(workbook);
        int rowIndex = 3;
        int stt = 1;

        for (Map.Entry<String, List<OrderReportByDayDTO>> entry : groupedData.entrySet()) {
            List<OrderReportByDayDTO> reports = entry.getValue();
            OrderReportByDayDTO firstReport = reports.get(0);

            Row row = sheet.createRow(rowIndex++);

            ExcelFormatUtils.createCell(row, 0, stt++, cellStyle);
            ExcelFormatUtils.createCell(row, 1, firstReport.getWarehouseId(), cellStyle);
            ExcelFormatUtils.createCell(row, 2, firstReport.getWarehouseName(), cellStyle);

            Map<String, Long> orderCountMap = reports.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getStoredAt().toLocalDate().format(DAY_FORMATTER),
                            Collectors.summingLong(OrderReportByDayDTO::getOrderCount)
                    ));

            int colIndex = 3;
            for (String date : dateHeaders) {
                ExcelFormatUtils.createCell(row, colIndex++, orderCountMap.getOrDefault(date, 0L), cellStyle);
            }
        }
    }

    public static void generateMonthlyReport(Sheet sheet, Workbook workbook,
                                             List<OrderReportByMonthDTO> reportData,
                                             List<String> dateHeaders) {
        Map<String, List<OrderReportByMonthDTO>> groupedData = reportData.stream()
                .collect(Collectors.groupingBy(OrderReportByMonthDTO::getWarehouseId));

        CellStyle cellStyle = ExcelFormatUtils.createCellStyle(workbook);
        int rowIndex = 3;
        int stt = 1;

        for (Map.Entry<String, List<OrderReportByMonthDTO>> entry : groupedData.entrySet()) {
            List<OrderReportByMonthDTO> reports = entry.getValue();
            OrderReportByMonthDTO firstReport = reports.get(0);

            Row row = sheet.createRow(rowIndex++);

            ExcelFormatUtils.createCell(row, 0, stt++, cellStyle);
            ExcelFormatUtils.createCell(row, 1, firstReport.getWarehouseId(), cellStyle);
            ExcelFormatUtils.createCell(row, 2, firstReport.getWarehouseName(), cellStyle);

            Map<String, Long> orderCountMap = reports.stream()
                    .collect(Collectors.toMap(
                            r -> r.getReportMonth().format(MONTH_FORMATTER),
                            OrderReportByMonthDTO::getOrderCount,
                            Long::sum
                    ));


            int colIndex = 3;
            for (String date : dateHeaders) {
                ExcelFormatUtils.createCell(row, colIndex++, orderCountMap.getOrDefault(date, 0L), cellStyle);
            }
        }
    }
}