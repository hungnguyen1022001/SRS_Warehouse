package com.hungnguyen.srs_warehouse.util;

import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.dto.orderCreate.SupplierRequest;
import com.hungnguyen.srs_warehouse.dto.orderCreate.ReceiverRequest;
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

    /**
     * Đọc file Excel và parse thành danh sách OrderRequest.
     * @param file      File Excel tải lên.
     * @param errorRows Danh sách lỗi khi đọc file.
     * @return Danh sách OrderRequest hợp lệ.
     */
    public static List<OrderRequest> parseExcelFile(MultipartFile file, List<Map<String, String>> errorRows) {
        List<OrderRequest> validOrders = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 2; i < rowCount; i++) { // Bỏ qua header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Kiểm tra nếu STT rỗng, kết thúc vòng lặp
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

    /**
     * Parse một hàng dữ liệu trong file Excel thành OrderRequest.
     * @param row       Hàng dữ liệu hiện tại.
     * @param rowIndex  Số thứ tự hàng trong file.
     * @param errorRows Danh sách lỗi nếu có.
     * @return Đối tượng OrderRequest hoặc null nếu có lỗi.
     */
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

    /**
     * Tạo file lỗi Excel chứa danh sách lỗi.
     * @param file      File Excel gốc.
     * @param errorRows Danh sách lỗi.
     * @return Đường dẫn file lỗi đã tạo.
     */
    public static String generateErrorFile(MultipartFile file, List<Map<String, String>> errorRows) {
        String fileName = "INB_ImportError_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        Path filePath = Paths.get(ERROR_FILE_DIR, fileName);

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is);
             FileOutputStream fos = new FileOutputStream(filePath.toFile())) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastColumn = sheet.getRow(1).getLastCellNum();

            sheet.getRow(1).createCell(lastColumn).setCellValue("Thông tin lỗi");

            for (Map<String, String> error : errorRows) {
                int rowIndex = Integer.parseInt(error.get("Row"));
                Row row = sheet.getRow(rowIndex - 1);
                if (row != null) {
                    row.createCell(lastColumn).setCellValue(error.get("Error"));
                }
            }

            workbook.write(fos);
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file lỗi Excel: " + e.getMessage(), e);
        }
    }

    // ✅ Các phương thức validate dữ liệu
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
        String phone = cell != null ? cell.toString().trim() : "";
        if (!phone.matches("^0\\d{9,10}$")) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không đúng định dạng (bắt đầu bằng 0, 10-11 chữ số)"));
        }
        return phone;
    }

    private static String validateEmail(Cell cell, String fieldName, int rowIndex, List<Map<String, String>> errorRows) {
        String email = cell != null ? cell.toString().trim().toLowerCase() : "";
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", fieldName + " không đúng định dạng email hợp lệ"));
        }
        return email;
    }

    private static BigDecimal validateBigDecimal(Cell cell, String fieldName, BigDecimal min, BigDecimal max, int rowIndex, List<Map<String, String>> errorRows) {
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