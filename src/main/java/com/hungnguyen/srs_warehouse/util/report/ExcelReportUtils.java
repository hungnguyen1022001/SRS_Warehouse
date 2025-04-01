package com.hungnguyen.srs_warehouse.util.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportUtils {
    private static final String TEMPLATE_FILE = "src/main/resources/templates/Report01.xlsx";
    private static final String SHEET_NAME = "BaoCao";

    public static byte[] createOrderReportFile(List<?> reportData, List<String> dateHeaders) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(TEMPLATE_FILE));
             Workbook workbook = new XSSFWorkbook(fis);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) throw new IllegalArgumentException("Sheet " + SHEET_NAME + " không tồn tại!");

            ExcelReportFillUitls.clearOldData(sheet);
            ExcelReportFillUitls.writeReportHeader(sheet, dateHeaders);
            ExcelReportFillUitls.writeReportData(sheet, reportData, dateHeaders, workbook);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static String generateFileName() {
        return "Report01_" + System.currentTimeMillis() + ".xlsx";
    }
}
