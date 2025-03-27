package com.hungnguyen.srs_warehouse.util.label;

import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.format.DateTimeFormatter;

public class ExcelLabelFillUtils {

    /**
     * Xóa dữ liệu cũ trong các ô cần làm mới.
     */
    public static void clearData(Sheet sheet) {
        int[][] cellsToClear = {
                {7, 1}, {8, 1}, {9, 1}, {10, 1}, {11, 1},
                {7, 9}, {8, 9}, {9, 9},
                {26, 11}, {26, 12}, {27, 11}, {27, 12},
                {11, 2}, {9, 10}
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

    /**
     * Điền thông tin nhãn đơn hàng vào sheet Excel.
     */
    public static void fillLabelSheet(XSSFSheet sheet, OrderLabelDTO dto, XSSFWorkbook workbook) {
        String orderDetailUrl = "http://localhost:5173/order/detail/" + dto.orderId();

        BarcodeFillUtils.insertBarcodeInRange(sheet, dto.orderId(), workbook);
        BarcodeFillUtils.insertQRCodeInRange(sheet, orderDetailUrl, workbook);

        // Thông tin người gửi
        writeCellValue(sheet, 7, 1, dto.supplier().name(), workbook, false, false, false, false);
        int senderLastRow = writeMultiLineText(sheet, 8, 1, dto.supplier().address(), workbook, 7);
        writeCellValue(sheet, senderLastRow + 1, 1, "SĐT: " + dto.supplier().phone(), workbook, false, false, false, false);

        // Thông tin người nhận
        writeCellValue(sheet, 7, 9, dto.receiver().name(), workbook, false, false, false, false);
        int receiverLastRow = writeMultiLineText(sheet, 8, 9, dto.receiver().address(), workbook, 14);
        writeCellValue(sheet, receiverLastRow + 1, 9, "SĐT: " + dto.receiver().phone(), workbook, false, false, false, false);

        // Mã đơn hàng
        writeCellValue(sheet, 3, 12, dto.orderId(), workbook, false, false, false, false);

        // Ngày giờ tạo đơn
        String orderDate = dto.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String orderTime = dto.createdAt().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        writeCellValue(sheet, 26, 11, orderDate, workbook, true, true, true, true);
        writeCellValue(sheet, 27, 11, orderTime, workbook, true, true, true, true);

        // Thông tin kho hàng
        writeCellValue(sheet, 17, 12, dto.warehouse().warehouseId(), workbook, true, true, true, false);
        writeCellValue(sheet, 17, 2, dto.warehouse().name(), workbook, true, false, true, false);
    }

    /**
     * Viết văn bản nhiều dòng vào một ô Excel.
     */
    private static int writeMultiLineText(Sheet sheet, int row, int col, String value, Workbook workbook, int colLimit) {
        if (value == null) return row;

        String[] words = value.split(" ");
        StringBuilder line = new StringBuilder();
        int currentCol = col;
        int currentRow = row;

        for (String word : words) {
            if (line.length() + word.length() > 30) {
                writeCellValue(sheet, currentRow, currentCol, line.toString(), workbook, false, false, false, false);
                line.setLength(0);
                currentRow++;

                if (currentCol >= colLimit) {
                    currentCol = col;
                    currentRow++;
                }
            }
            line.append(word).append(" ");
        }

        if (!line.isEmpty()) {
            writeCellValue(sheet, currentRow, currentCol, line.toString().trim(), workbook, false, false, false, false);
        }

        return currentRow;
    }

    /**
     * Thiết lập giá trị và style cho một ô trong sheet.
     */
    private static void writeCellValue(Sheet sheet, int row, int col, String value, Workbook workbook,
                                       boolean isBold, boolean isHorizontalCenterAligned,
                                       boolean isVerticalCenterAligned, boolean isLargeFont) {
        if (value == null) return;

        Row targetRow = sheet.getRow(row) != null ? sheet.getRow(row) : sheet.createRow(row);
        Cell cell = targetRow.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(createCellStyle(workbook, isBold, isHorizontalCenterAligned, isVerticalCenterAligned, isLargeFont));
    }

    /**
     * Tạo style cho ô trong Excel.
     */
    private static CellStyle createCellStyle(Workbook workbook, boolean isBold, boolean isHorizontalCenterAligned,
                                             boolean isVerticalCenterAligned, boolean isLargeFont) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");

        font.setFontHeightInPoints(isLargeFont ? (short) 11 : isBold ? (short) 12 : (short) 9);
        font.setBold(isBold);
        style.setFont(font);

        if (isHorizontalCenterAligned) {
            style.setAlignment(HorizontalAlignment.CENTER);
        }

        style.setVerticalAlignment(isVerticalCenterAligned ? VerticalAlignment.CENTER : VerticalAlignment.BOTTOM);

        return style;
    }
}
