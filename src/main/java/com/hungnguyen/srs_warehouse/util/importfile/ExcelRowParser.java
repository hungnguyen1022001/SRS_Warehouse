package com.hungnguyen.srs_warehouse.util.importfile;

import com.hungnguyen.srs_warehouse.dto.orderCreate.OrderRequest;
import com.hungnguyen.srs_warehouse.dto.orderCreate.ReceiverRequest;
import com.hungnguyen.srs_warehouse.dto.orderCreate.SupplierRequest;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExcelRowParser {

    public static OrderRequest parseRow(Row row, int rowIndex, List<Map<String, String>> errorRows) {
        SupplierRequest supplier = new SupplierRequest(
                ExcelValidator.validateString(row.getCell(1), "Tên NCC", 50, rowIndex, errorRows),
                ExcelValidator.validateString(row.getCell(4), "Địa chỉ NCC", 200, rowIndex, errorRows),
                ExcelValidator.validatePhone(row.getCell(2), "SĐT NCC", rowIndex, errorRows),
                ExcelValidator.validateEmail(row.getCell(3), "Email NCC", rowIndex, errorRows),
                ExcelValidator.validateBigDecimal(row.getCell(5), "Vĩ độ NCC", new BigDecimal("-90"), new BigDecimal("90"), rowIndex, errorRows),
                ExcelValidator.validateBigDecimal(row.getCell(6), "Kinh độ NCC", new BigDecimal("-180"), new BigDecimal("180"), rowIndex, errorRows)
        );

        ReceiverRequest receiver = new ReceiverRequest(
                ExcelValidator.validateString(row.getCell(7), "Tên BNH", 50, rowIndex, errorRows),
                ExcelValidator.validateString(row.getCell(10), "Địa chỉ BNH", 200, rowIndex, errorRows),
                ExcelValidator.validatePhone(row.getCell(8), "SĐT BNH", rowIndex, errorRows),
                ExcelValidator.validateEmail(row.getCell(9), "Email BNH", rowIndex, errorRows),
                ExcelValidator.validateBigDecimal(row.getCell(11), "Vĩ độ BNH", new BigDecimal("-90"), new BigDecimal("90"), rowIndex, errorRows),
                ExcelValidator.validateBigDecimal(row.getCell(12), "Kinh độ BNH", new BigDecimal("-180"), new BigDecimal("180"), rowIndex, errorRows)
        );

        return new OrderRequest(supplier, receiver);
    }
}
