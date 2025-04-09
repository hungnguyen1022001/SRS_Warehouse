package com.hungnguyen.srs_warehouse.util.report;

import com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO;
import com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ExcelReportUtils {
    private static final String TEMPLATE_FILE = "src/main/resources/templates/Report01_yyyyMMddHHMMSS.xlsx";
    private static final String SHEET_NAME = "BaoCao";

    public static byte[] createOrderReportFile(List<?> reportData, List<String> dateHeaders) throws IOException {
        try (FileInputStream fis = new FileInputStream(TEMPLATE_FILE);
             Workbook workbook = new XSSFWorkbook(fis);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet " + SHEET_NAME + " không tồn tại!");
            }

            if (!reportData.isEmpty() && !dateHeaders.isEmpty()) {
                ExcelFormatUtils.clearOldData(sheet, workbook);
                ExcelFormatUtils.setReportPeriodTitle(sheet, reportData, dateHeaders);
                ExcelFormatUtils.addDateColumnHeaders(sheet, dateHeaders);

                if (reportData.get(0) instanceof OrderReportByDayDTO) {
                    ReportGenerators.generateDailyReport(sheet, workbook,
                            (List<OrderReportByDayDTO>) reportData, dateHeaders);
                } else if (reportData.get(0) instanceof OrderReportByMonthDTO) {
                    ReportGenerators.generateMonthlyReport(sheet, workbook,
                            (List<OrderReportByMonthDTO>) reportData, dateHeaders);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static String generateFileName() {
        return "Report01_" + System.currentTimeMillis() + ".xlsx";
    }
}