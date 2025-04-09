package com.hungnguyen.srs_warehouse.util.report;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class ExcelFormatUtils {

    public static void clearOldData(Sheet sheet, Workbook workbook) {
        for (int rowIndex = 2; rowIndex <= 12; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                int startCol = (rowIndex == 2) ? 3 : 0;
                for (int colIndex = startCol; colIndex <= 17; colIndex++) {
                    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue("");
                    cell.setCellStyle(workbook.createCellStyle());
                }
            }
        }
    }

    public static void setReportPeriodTitle(Sheet sheet, List<?> reportData, List<String> dateHeaders) {
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

    public static void addDateColumnHeaders(Sheet sheet, List<String> dateHeaders) {
        Row columnHeaderRow = sheet.getRow(2);
        if (columnHeaderRow == null) {
            columnHeaderRow = sheet.createRow(2);
        }

        Workbook workbook = sheet.getWorkbook();
        CellStyle headerStyle = createCellStyle(workbook);

        int colIndex = 3;
        for (String date : dateHeaders) {
            Cell cell = columnHeaderRow.createCell(colIndex++);
            cell.setCellValue(date);
            cell.setCellStyle(headerStyle);
        }
    }

    public static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        cell.setCellStyle(style);
    }

    public static CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}