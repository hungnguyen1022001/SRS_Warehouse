package com.hungnguyen.srs_warehouse.util;

import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.OrderRequest;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.SupplierRequest;
import com.hungnguyen.srs_warehouse.model.DTO.ordercreate.ReceiverRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelUtils {

    private static final String ERROR_FILE_DIR = System.getProperty("user.home") + "/Desktop";

    public static List<OrderRequest> parseExcelFile(MultipartFile file, List<Map<String, String>> errorRows) {
        List<OrderRequest> validOrders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 2; i < rowCount; i++) { // Bỏ qua hàng tiêu đề (Hàng 1 & 2)
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Nếu STT không hợp lệ hoặc rỗng, dừng xử lý file
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
                    break;
                }

                try {
                    OrderRequest orderRequest = parseOrderRow(row, i + 1, errorRows);
                    if (orderRequest != null) {
                        validOrders.add(orderRequest);
                    }
                } catch (Exception e) {
                    errorRows.add(Map.of("Row", String.valueOf(i + 1), "Error", e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }

        return validOrders;
    }

    private static OrderRequest parseOrderRow(Row row, int rowIndex, List<Map<String, String>> errorRows) {
        try {
            SupplierRequest supplier = new SupplierRequest(
                    validateString(row.getCell(1), "Tên NCC", 50, rowIndex, errorRows),
                    validateString(row.getCell(4), "Địa chỉ NCC", 200, rowIndex, errorRows),
                    validatePhone(row.getCell(2), "SĐT NCC", rowIndex, errorRows),
                    validateEmail(row.getCell(3), "Email NCC", rowIndex, errorRows),
                    validateBigDecimal(row.getCell(5), "Vĩ độ NCC", new BigDecimal("-90.000000"), new BigDecimal("90.000000"), rowIndex, errorRows),
                    validateBigDecimal(row.getCell(6), "Kinh độ NCC", new BigDecimal("-180.000000"), new BigDecimal("180.000000"), rowIndex, errorRows)
            );

            ReceiverRequest receiver = new ReceiverRequest(
                    validateString(row.getCell(7), "Tên BNH", 50, rowIndex, errorRows),
                    validateString(row.getCell(10), "Địa chỉ BNH", 200, rowIndex, errorRows),
                    validatePhone(row.getCell(8), "SĐT BNH", rowIndex, errorRows),
                    validateEmail(row.getCell(9), "Email BNH", rowIndex, errorRows),
                    validateBigDecimal(row.getCell(11), "Vĩ độ BNH", new BigDecimal("-90.000000"), new BigDecimal("90.000000"), rowIndex, errorRows),
                    validateBigDecimal(row.getCell(12), "Kinh độ BNH", new BigDecimal("-180.000000"), new BigDecimal("180.000000"), rowIndex, errorRows)
            );

            return new OrderRequest(supplier, receiver);
        } catch (Exception e) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", e.getMessage()));
            return null;
        }
    }

    public static String generateErrorFile(MultipartFile file, List<Map<String, String>> errorRows) {
        String fileName = "INB_ImportError_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        Path filePath = Paths.get(ERROR_FILE_DIR, fileName);

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is);
             FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(1);
            Cell errorHeaderCell = headerRow.createCell(headerRow.getLastCellNum());
            errorHeaderCell.setCellValue("Thông tin lỗi");

            for (Map<String, String> error : errorRows) {
                int rowIndex = Integer.parseInt(error.get("Row"));
                Row row = sheet.getRow(rowIndex - 1);
                if (row != null) {
                    Cell errorCell = row.createCell(row.getLastCellNum());
                    errorCell.setCellValue(error.get("Error"));
                }
            }

            workbook.write(fos);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file lỗi Excel: " + e.getMessage(), e);
        }
    }

    private static String validateString(Cell cell, String fieldName, int maxLength, int rowIndex, List<Map<String, String>> errorRows) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không được để trống"));
            return "";
        }
        String value = cell.getStringCellValue().trim();
        if (value.length() > maxLength) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " vượt quá " + maxLength + " ký tự"));
        }
        return value;
    }

    private static String validatePhone(Cell cell, String fieldName, int rowIndex, List<Map<String, String>> errorRows) {
        String phone = "";

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không được để trống"));
        } else {
            if (cell.getCellType() == CellType.NUMERIC) {
                phone = new java.text.DecimalFormat("0").format(cell.getNumericCellValue()); // Giữ số 0 đầu
            } else {
                phone = cell.getStringCellValue().trim();
            }
        }

        if (!phone.matches("^0\\d{9,10}$")) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không đúng định dạng (bắt đầu bằng 0, 10-11 chữ số)"));
        }

        return phone;
    }

    private static String validateEmail(Cell cell, String fieldName, int rowIndex, List<Map<String, String>> errorRows) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không được để trống"));
            return "";
        }
        String email = cell.getStringCellValue().trim().toLowerCase();

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không đúng định dạng email hợp lệ"));
        }

        return email;
    }

    private static BigDecimal validateBigDecimal(Cell cell, String fieldName, BigDecimal min, BigDecimal max, int rowIndex, List<Map<String, String>> errorRows) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không được để trống"));
            return BigDecimal.ZERO;
        }
        try {
            BigDecimal value = new BigDecimal(cell.toString().trim());
            if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " phải nằm trong khoảng [" + min + ", " + max + "]"));
            }
            return value;
        } catch (Exception e) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " phải là số hợp lệ"));
            return BigDecimal.ZERO;
        }
    }
}
