package com.hungnguyen.srs_warehouse.util;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelReportUtils {

    private static final String TEMPLATE_PATH = "src/main/resources/templates/Report01_yyyyMMddHHMMSS.xlsx";

    public static byte[] createReportFile(List<Object[]> reportData, boolean isMonthly, LocalDate startDate, LocalDate endDate) throws IOException {
        try (InputStream templateStream = new FileInputStream(TEMPLATE_PATH);
             Workbook workbook = new XSSFWorkbook(templateStream)) {

            Sheet sheet = workbook.getSheet("BaoCao");

            // Tạo danh sách ngày hoặc tháng theo khoảng thời gian
            List<String> timePeriods = isMonthly ? getMonthsBetween(startDate, endDate) : getDaysBetween(startDate, endDate);

            // Cập nhật tiêu đề ngày/tháng trong file mẫu
            Row headerRow = sheet.getRow(2);
            int dateColumnStart = 4;
            for (int i = 0; i < timePeriods.size(); i++) {
                Cell cell = headerRow.getCell(dateColumnStart + i);
                if (cell == null) {
                    cell = headerRow.createCell(dateColumnStart + i);
                }
                cell.setCellValue(timePeriods.get(i));
            }

            // Điền dữ liệu vào sheet
            int rowNum = 3;
            int stt = 1;
            for (Object[] row : reportData) {
                Row dataRow = sheet.getRow(rowNum);
                if (dataRow == null) {
                    dataRow = sheet.createRow(rowNum);
                }

                dataRow.createCell(0).setCellValue(stt++); // STT
                dataRow.createCell(1).setCellValue(row[0].toString()); // Mã kho
                dataRow.createCell(2).setCellValue(row[1].toString()); // Tên kho
                dataRow.createCell(3).setCellValue(Integer.parseInt(row[2].toString())); // Tổng số đơn

                // Điền dữ liệu từng ngày/tháng
                for (int i = 0; i < timePeriods.size(); i++) {
                    Cell cell = dataRow.createCell(dateColumnStart + i);
                    if (row.length > (3 + i) && row[3 + i] != null) {
                        cell.setCellValue(Integer.parseInt(row[3 + i].toString()));
                    } else {
                        cell.setCellValue(0);
                    }
                }
                rowNum++;
            }

            // Xuất file Excel
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static List<String> getDaysBetween(LocalDate startDate, LocalDate endDate) {
        return IntStream.rangeClosed(0, (int) startDate.until(endDate).getDays())
                .mapToObj(i -> startDate.plusDays(i).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toList());
    }

    private static List<String> getMonthsBetween(LocalDate startDate, LocalDate endDate) {
        return IntStream.rangeClosed(0, (int) YearMonth.from(startDate).until(endDate, java.time.temporal.ChronoUnit.MONTHS))
                .mapToObj(i -> startDate.plusMonths(i).format(DateTimeFormatter.ofPattern("MM/yyyy")))
                .collect(Collectors.toList());
    }
}

