package com.hungnguyen.srs_warehouse.util;

import com.hungnguyen.srs_warehouse.model.DTO.orderDetail.OrderLabelDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelLabelUtils {

    private static final String TEMPLATE_PATH = "src/main/resources/templates/LabelTemp.xlsx";

    public static byte[] createLabelFile(List<OrderLabelDTO> orders) throws IOException {
        try (InputStream templateStream = new FileInputStream(TEMPLATE_PATH);
             Workbook workbook = new XSSFWorkbook(templateStream)) {
            for (OrderLabelDTO dto : orders) {
                processOrderSheet(workbook, dto);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void processOrderSheet(Workbook workbook, OrderLabelDTO dto) {
        Sheet templateSheet = workbook.getSheetAt(0);
        Sheet sheet = workbook.createSheet(dto.orderId());

        copySheet(templateSheet, sheet);
        clearData(sheet);

        int senderLastRow = setMultiLineValue(sheet, 8, 1, dto.supplier().address(), workbook);
        int receiverLastRow = setMultiLineValue(sheet, 8, 9, dto.receiver().address(), workbook);

        setValue(sheet, senderLastRow + 1, 1, "SĐT: " + dto.supplier().phone(), workbook);
        setValue(sheet, receiverLastRow + 1, 9, "SĐT: " + dto.receiver().phone(), workbook);

        setValue(sheet, 3, 12, dto.orderId(), workbook);
        setValue(sheet, 25, 11, dto.orderId(), workbook);
        setValue(sheet, 25, 12, dto.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), workbook);

        setValue(sheet, 7, 1, dto.supplier().name(), workbook);
        setValue(sheet, 7, 9, dto.receiver().name(), workbook);

        setValue(sheet, 17, 2, dto.warehouse().name(), workbook);
        setValue(sheet, 17, 12, dto.warehouse().warehouseId(), workbook);
    }

    private static void clearData(Sheet sheet) {
        int[][] cellsToClear = {
                {7, 1}, {8, 1}, {9, 1}, {10, 1}, {11, 1},
                {7, 9}, {8, 9}, {9, 9},
                {17, 2}, {17, 12},
                {25, 11}, {25, 12}, {26, 11}, {26, 12},
                {11, 2},
                {9, 10}
        };
        for (int[] cell : cellsToClear) {
            clearCell(sheet, cell[0], cell[1]);
        }
    }

    private static void clearCell(Sheet sheet, int row, int col) {
        Row targetRow = sheet.getRow(row);
        if (targetRow != null) {
            Cell cell = targetRow.getCell(col);
            if (cell != null) {
                cell.setCellValue("");
            }
        }
    }

    private static int setMultiLineValue(Sheet sheet, int row, int col, String value, Workbook workbook) {
        if (value == null) return row;

        String[] lines = value.split("\n");
        for (int i = 0; i < lines.length; i++) {
            setValue(sheet, row + i, col, lines[i], workbook);
        }
        return row + lines.length - 1;
    }

    private static void setValue(Sheet sheet, int row, int col, String value, Workbook workbook) {
        if (value == null) return;
        Row targetRow = sheet.getRow(row);
        if (targetRow == null) targetRow = sheet.createRow(row);
        Cell cell = targetRow.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(defaultCellStyle(workbook));
    }

    private static CellStyle defaultCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }

    private static void copySheet(Sheet source, Sheet target) {
        for (int i = 0; i < source.getRow(0).getLastCellNum(); i++) {
            target.setColumnWidth(i, source.getColumnWidth(i));
        }
        for (int i = 0; i <= source.getLastRowNum(); i++) {
            Row sourceRow = source.getRow(i);
            Row targetRow = target.createRow(i);
            if (sourceRow != null) {
                targetRow.setHeight(sourceRow.getHeight());
                for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                    Cell sourceCell = sourceRow.getCell(j);
                    if (sourceCell != null) {
                        Cell targetCell = targetRow.createCell(j);
                        targetCell.setCellValue(sourceCell.toString());
                        CellStyle newCellStyle = target.getWorkbook().createCellStyle();
                        newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                        targetCell.setCellStyle(newCellStyle);
                    }
                }
            }
        }
    }
}
