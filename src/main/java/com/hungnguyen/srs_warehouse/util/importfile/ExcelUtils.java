package com.hungnguyen.srs_warehouse.util.importfile;

import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class for parsing and generating Excel files related to order imports.
 */
public class ExcelUtils {

    private static final int ERROR_COLUMN_INDEX = 13;

    /**
     * Parses the given Excel file into a list of valid OrderRequest objects and captures error rows.
     *
     * @param file      The Excel file uploaded.
     * @param errorRows A list to collect rows with parsing errors.
     * @return A list of valid OrderRequest instances.
     */
    public static List<OrderRequest> parseExcelFile(MultipartFile file, List<Map<String, String>> errorRows) {
        List<OrderRequest> validOrders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                try {
                    OrderRequest order = ExcelRowParser.parseRow(row, i + 1, errorRows);
                    if (order != null) validOrders.add(order);
                } catch (Exception e) {
                    errorRows.add(Map.of(
                            "Row", String.valueOf(i + 1),
                            "Error", e.getMessage() != null ? e.getMessage() : "Unknown error"
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel", e);
        }

        return validOrders;
    }

    /**
     * Generates a downloadable Excel file containing error details marked in red in the original file.
     *
     * @param file      The original Excel file.
     * @param errorRows The list of error details by row.
     * @return A record containing the error file name and byte content.
     */
    public static ExcelErrorFile generateErrorFileBytes(MultipartFile file, List<Map<String, String>> errorRows) {
        String fileName = "INB_ImportError_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(1);
            if (headerRow == null) {
                headerRow = sheet.createRow(1);
            }
            headerRow.createCell(ERROR_COLUMN_INDEX).setCellValue("Thông tin lỗi");

            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setColor(IndexedColors.RED.getIndex());
            style.setFont(font);

            for (Map<String, String> error : errorRows) {
                int rowIndex = Integer.parseInt(error.get("Row"));
                Row row = sheet.getRow(rowIndex - 1);
                if (row != null) {
                    Cell cell = row.createCell(ERROR_COLUMN_INDEX);
                    cell.setCellValue(error.get("Error"));
                    cell.setCellStyle(style);
                }
            }

            workbook.write(out);
            return new ExcelErrorFile(fileName, out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo file lỗi Excel", e);
        }
    }

    /**
     * Container for the generated Excel file's name and content as bytes.
     *
     * @param filename The name of the generated error file.
     * @param content  The byte content of the error Excel file.
     */
    public record ExcelErrorFile(String filename, byte[] content) {}
}
