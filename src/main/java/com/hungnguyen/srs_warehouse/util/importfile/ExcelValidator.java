package com.hungnguyen.srs_warehouse.util.importfile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExcelValidator {

    public static String validateString(Cell cell, String fieldName, int maxLength, int rowIndex, List<Map<String, String>> errorRows) {
        String value = (cell != null) ? cell.toString().trim() : "";
        if (value.isEmpty()) {
            addError(errorRows, rowIndex, fieldName + " không được để trống");
        } else if (value.length() > maxLength) {
            addError(errorRows, rowIndex, fieldName + " vượt quá " + maxLength + " ký tự");
        }
        return value;
    }

    public static String validatePhone(Cell cell, String fieldName, int rowIndex, List<Map<String, String>> errorRows) {
        String phone = (cell != null) ? cell.toString().trim() : "";
        if (phone.isEmpty()) {
            addError(errorRows, rowIndex, fieldName + " không được để trống");
        } else if (!phone.matches("^0\\d{9,10}$")) {
            addError(errorRows, rowIndex, fieldName + " không đúng định dạng SĐT (bắt đầu bằng 0, 10-11 số)");
        }
        return phone;
    }

    public static String validateEmail(Cell cell, String fieldName, int rowIndex, List<Map<String, String>> errorRows) {
        String email = (cell != null) ? cell.toString().trim().toLowerCase() : "";
        if (email.isEmpty()) {
            addError(errorRows, rowIndex, fieldName + " không được để trống");
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            addError(errorRows, rowIndex, fieldName + " không đúng định dạng email");
        }
        return email;
    }

    public static BigDecimal validateBigDecimal(Cell cell, String fieldName, BigDecimal min, BigDecimal max, int rowIndex, List<Map<String, String>> errorRows) {
        try {
            BigDecimal value = (cell.getCellType() == CellType.NUMERIC)
                    ? BigDecimal.valueOf(cell.getNumericCellValue())
                    : new BigDecimal(cell.toString().trim());

            if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                addError(errorRows, rowIndex, fieldName + " phải nằm trong khoảng [" + min + ", " + max + "]");
            }

            return value;
        } catch (Exception e) {
            addError(errorRows, rowIndex, fieldName + " phải là số hợp lệ");
            return BigDecimal.ZERO;
        }
    }

    private static void addError(List<Map<String, String>> errorRows, int rowIndex, String message) {
        errorRows.add(Map.of("Row", String.valueOf(rowIndex), "Error", message));
    }
}
