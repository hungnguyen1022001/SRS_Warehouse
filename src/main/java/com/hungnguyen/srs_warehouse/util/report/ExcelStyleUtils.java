package com.hungnguyen.srs_warehouse.util.report;

import org.apache.poi.ss.usermodel.*;

public class ExcelStyleUtils {

    public static CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    public static void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof String) cell.setCellValue((String) value);
        else if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
        cell.setCellStyle(style);
    }
}
