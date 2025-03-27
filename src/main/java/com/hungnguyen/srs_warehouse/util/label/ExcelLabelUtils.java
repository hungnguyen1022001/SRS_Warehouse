package com.hungnguyen.srs_warehouse.util.label;

import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class ExcelLabelUtils {

    private static final String TEMPLATE_PATH = "src/main/resources/templates/LabelTemp.xlsx";

    /**
     * Tạo file Excel nhãn đơn hàng từ danh sách OrderLabelDTO.
     *
     * @param orders Danh sách đơn hàng cần tạo nhãn.
     * @return Mảng byte chứa dữ liệu file Excel.
     * @throws IOException Nếu có lỗi trong quá trình đọc/ghi file.
     */
    public static byte[] createLabelFile(List<OrderLabelDTO> orders) throws IOException {
        try (InputStream templateStream = new FileInputStream(TEMPLATE_PATH);
             XSSFWorkbook workbook = new XSSFWorkbook(templateStream)) {

            XSSFSheet templateSheet = workbook.getSheetAt(0);

            for (OrderLabelDTO dto : orders) {
                XSSFSheet sheet = ExcelSheetCopyUtils.createSheetFromTemplate(workbook, templateSheet, dto.orderId());

                ExcelLabelFillUtils.clearData(sheet);
                ExcelLabelFillUtils.fillLabelSheet(sheet, dto, workbook);
            }

            int sheetIndex = workbook.getSheetIndex("Sheet1");
            if (sheetIndex != -1) {
                workbook.removeSheetAt(sheetIndex);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}