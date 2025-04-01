package com.hungnguyen.srs_warehouse.util.report;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelReportUtils {
    private static final String TEMPLATE_FILE = "src/main/resources/templates/Report01_yyyyMMddHHMMSS.xlsx";
    private static final String SHEET_NAME = "BaoCao";

    public static byte[] createOrderReportFile(List<? extends Object> reportData, List<String> dateHeaders) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(TEMPLATE_FILE));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet " + SHEET_NAME + " không tồn tại!");
            }

            clearOldData(sheet, workbook);

            if (!dateHeaders.isEmpty() && !reportData.isEmpty()) {
                String fromDate = dateHeaders.get(0);
                String toDate = dateHeaders.get(dateHeaders.size() - 1);

                String reportPeriod;
                if (reportData.get(0) instanceof OrderReportByDayDTO) {
                    reportPeriod = "Từ ngày " + fromDate + " đến ngày " + toDate;
                } else if (reportData.get(0) instanceof OrderReportByMonthDTO) {
                    reportPeriod = "Từ tháng " + fromDate + " đến tháng " + toDate;
                } else {
                    reportPeriod = "Khoảng thời gian: " + fromDate + " - " + toDate;
                }

                Row headerRow = sheet.getRow(1);
                if (headerRow != null) {
                    Cell timeCell = headerRow.getCell(4, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    timeCell.setCellValue(reportPeriod);
                }
            }


            Row columnHeaderRow = sheet.getRow(2);
            int colIndex = 3;
            for (String date : dateHeaders) {
                Cell cell = columnHeaderRow.createCell(colIndex++);
                cell.setCellValue(date);
            }

            int rowIndex = 3;
            if (!reportData.isEmpty()) {
                if (reportData.get(0) instanceof OrderReportByDayDTO) {
                    writeDailyReportData(sheet, (List<OrderReportByDayDTO>) reportData, dateHeaders, rowIndex, workbook);
                } else if (reportData.get(0) instanceof OrderReportByMonthDTO) {
                    writeMonthlyReportData(sheet, (List<OrderReportByMonthDTO>) reportData, dateHeaders, rowIndex, workbook);
                }
            }

            applyBorders(sheet, rowIndex, colIndex, workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void clearOldData(Sheet sheet, Workbook workbook) {
        for (int rowIndex = 2; rowIndex <= 12; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                for (int colIndex = 0; colIndex <= 17; colIndex++) {
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("");
                    cell.setCellStyle(workbook.createCellStyle());
                }
            }
        }
    }

    private static void writeDailyReportData(Sheet sheet, List<OrderReportByDayDTO> reportData, List<String> dateHeaders, int rowIndex, Workbook workbook) {
        Map<String, List<OrderReportByDayDTO>> groupedData = reportData.stream()
                .collect(Collectors.groupingBy(OrderReportByDayDTO::getWarehouseId));

        CellStyle cellStyle = createCellStyle(workbook);
        int stt = 1;

        for (Map.Entry<String, List<OrderReportByDayDTO>> entry : groupedData.entrySet()) {
            String warehouseId = entry.getKey();
            List<OrderReportByDayDTO> reports = entry.getValue();
            OrderReportByDayDTO firstReport = reports.get(0);

            Row row = sheet.createRow(rowIndex++);
            createCell(row, 0, stt++, cellStyle);
            createCell(row, 1, firstReport.getWarehouseId(), cellStyle);
            createCell(row, 2, firstReport.getWarehouseName(), cellStyle);

            Map<String, Long> orderCountMap = reports.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getStoredAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            Collectors.summingLong(OrderReportByDayDTO::getOrderCount)
                    ));

            int colIndex = 3;
            for (String date : dateHeaders) {
                createCell(row, colIndex++, orderCountMap.getOrDefault(date, 0L), cellStyle);
            }

            applyRowBorders(row, colIndex, workbook);
        }
    }

    private static void writeMonthlyReportData(Sheet sheet, List<OrderReportByMonthDTO> reportData, List<String> dateHeaders, int rowIndex, Workbook workbook) {
        Map<String, List<OrderReportByMonthDTO>> groupedData = reportData.stream()
                .collect(Collectors.groupingBy(OrderReportByMonthDTO::getWarehouseId));

        CellStyle cellStyle = createCellStyle(workbook);
        int stt = 1;

        for (Map.Entry<String, List<OrderReportByMonthDTO>> entry : groupedData.entrySet()) {
            String warehouseId = entry.getKey();
            List<OrderReportByMonthDTO> reports = entry.getValue();
            OrderReportByMonthDTO firstReport = reports.get(0);

            Row row = sheet.createRow(rowIndex++);
            createCell(row, 0, stt++, cellStyle);
            createCell(row, 1, firstReport.getWarehouseId(), cellStyle);
            createCell(row, 2, firstReport.getWarehouseName(), cellStyle);

            Map<String, Long> orderCountMap = reports.stream()
                    .collect(Collectors.toMap(
                            r -> r.getReportMonth().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                            OrderReportByMonthDTO::getOrderCount
                    ));

            int colIndex = 3;
            for (String date : dateHeaders) {
                createCell(row, colIndex++, orderCountMap.getOrDefault(date, 0L), cellStyle);
            }

            applyRowBorders(row, colIndex, workbook);
        }
    }

    private static void applyRowBorders(Row row, int lastCol, Workbook workbook) {
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);

        for (int j = 0; j < lastCol; j++) {
            Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellStyle(borderStyle);
        }
    }

    private static void applyBorders(Sheet sheet, int lastRow, int lastCol, Workbook workbook) {
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 2; i < lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < lastCol; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellStyle(borderStyle);
                }
            }
        }
    }

    private static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        cell.setCellStyle(style);
    }

    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    public static String generateFileName() {
        return "Report01_" + System.currentTimeMillis() + ".xlsx";
    }
}
