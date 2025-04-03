package com.hungnguyen.srs_warehouse.util.label;

import com.hungnguyen.srs_warehouse.exception.CustomExceptions;
import com.hungnguyen.srs_warehouse.dto.orderDetail.OrderLabelDTO;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ExcelLabelUtils {

    private static final String TEMPLATE_PATH = "/templates/LabelTemp.xlsx";

    /**
     * Tạo file Excel nhãn đơn hàng từ danh sách OrderLabelDTO.
     *
     * @param orders Danh sách đơn hàng cần tạo nhãn.
     * @return Mảng byte chứa dữ liệu file Excel.
     */
    public static byte[] createLabelFile(List<OrderLabelDTO> orders) {
        try (InputStream templateStream = validateTemplate();
             XSSFWorkbook workbook = new XSSFWorkbook(templateStream);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            fillOrdersToWorkbook(workbook, orders);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CustomExceptions.FileGenerationException("EXCEL_GENERATE_ERROR");
        }
    }

    private static InputStream validateTemplate() {
        InputStream templateStream = ExcelLabelUtils.class.getResourceAsStream(TEMPLATE_PATH);
        if (templateStream == null) {
            throw new CustomExceptions.FileGenerationException("EXCEL_FILE_NOT_FOUND");
        }
        return templateStream;
    }

    /**
     * Điền dữ liệu đơn hàng vào workbook.
     *
     * @param workbook Workbook đang thao tác.
     * @param orders   Danh sách đơn hàng cần điền.
     */
    private static void fillOrdersToWorkbook(XSSFWorkbook workbook, List<OrderLabelDTO> orders) {
        try {
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
        } catch (IllegalArgumentException e) {
            throw new CustomExceptions.FileGenerationException("EXCEL_IMPORT_ERROR");
        }
    }
}
