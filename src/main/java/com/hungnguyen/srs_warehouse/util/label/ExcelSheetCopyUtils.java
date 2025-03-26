package com.hungnguyen.srs_warehouse.util.label;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

/**
 * Tiện ích sao chép toàn bộ nội dung và định dạng từ một sheet Excel sang sheet khác.
 */
public class ExcelSheetCopyUtils {

    /**
     * Sao chép toàn bộ nội dung và định dạng từ sheet nguồn sang sheet đích.
     *
     * @param sourceSheet Sheet nguồn
     * @param targetSheet Sheet đích
     * @param workbook Workbook chứa các sheet
     */
    public static void copySheetFully(Sheet sourceSheet, Sheet targetSheet, Workbook workbook) {
        copyColumnWidths(sourceSheet, targetSheet);
        copyMergedRegions(sourceSheet, targetSheet);
        copyRowsAndCells(sourceSheet, targetSheet, workbook);
        copyImages(sourceSheet, targetSheet, workbook);
    }

    /**
     * Sao chép độ rộng cột từ sheet nguồn sang sheet đích.
     */
    private static void copyColumnWidths(Sheet sourceSheet, Sheet targetSheet) {
        for (int i = 0; i < sourceSheet.getRow(0).getLastCellNum(); i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    /**
     * Sao chép các vùng hợp nhất ô từ sheet nguồn sang sheet đích.
     */
    private static void copyMergedRegions(Sheet sourceSheet, Sheet targetSheet) {
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            targetSheet.addMergedRegion(sourceSheet.getMergedRegion(i));
        }
    }

    /**
     * Sao chép từng dòng và từng ô từ sheet nguồn sang sheet đích.
     */
    private static void copyRowsAndCells(Sheet sourceSheet, Sheet targetSheet, Workbook workbook) {
        for (int rowNum = 0; rowNum <= sourceSheet.getLastRowNum(); rowNum++) {
            Row sourceRow = sourceSheet.getRow(rowNum);
            if (sourceRow == null) continue;

            Row targetRow = targetSheet.createRow(rowNum);
            targetRow.setHeight(sourceRow.getHeight());

            for (int colNum = 0; colNum < sourceRow.getLastCellNum(); colNum++) {
                Cell sourceCell = sourceRow.getCell(colNum);
                if (sourceCell == null) continue;

                Cell targetCell = targetRow.createCell(colNum);
                copyCellValue(sourceCell, targetCell);
                copyCellStyle(workbook, sourceCell, targetCell);
            }
        }
    }

    /**
     * Sao chép giá trị của ô từ nguồn sang đích.
     */
    private static void copyCellValue(Cell sourceCell, Cell targetCell) {
        switch (sourceCell.getCellType()) {
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(sourceCell)) {
                    targetCell.setCellValue(sourceCell.getDateCellValue());
                } else {
                    targetCell.setCellValue(sourceCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            default:
                targetCell.setCellValue("");
        }
    }

    /**
     * Sao chép định dạng của ô từ nguồn sang đích.
     */
    private static void copyCellStyle(Workbook workbook, Cell sourceCell, Cell targetCell) {
        CellStyle targetStyle = workbook.createCellStyle();
        targetStyle.cloneStyleFrom(sourceCell.getCellStyle());
        targetCell.setCellStyle(targetStyle);
    }

    /**
     * Sao chép hình ảnh từ sheet nguồn sang sheet đích.
     */
    private static void copyImages(Sheet sourceSheet, Sheet targetSheet, Workbook workbook) {
        if (!(workbook instanceof XSSFWorkbook)) return;
        XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
        XSSFSheet xssfSourceSheet = (XSSFSheet) sourceSheet;
        XSSFSheet xssfTargetSheet = (XSSFSheet) targetSheet;

        XSSFDrawing drawing = xssfSourceSheet.getDrawingPatriarch();
        if (drawing == null) return;

        XSSFDrawing targetDrawing = xssfTargetSheet.createDrawingPatriarch();

        for (XSSFShape shape : drawing.getShapes()) {
            if (shape instanceof XSSFPicture) {
                XSSFPicture picture = (XSSFPicture) shape;
                XSSFPictureData pictureData = picture.getPictureData();
                int pictureIndex = xssfWorkbook.addPicture(pictureData.getData(), pictureData.getPictureType());

                XSSFClientAnchor anchor = (XSSFClientAnchor) picture.getAnchor();
                XSSFClientAnchor newAnchor = new XSSFClientAnchor(
                        anchor.getDx1(), anchor.getDy1(), anchor.getDx2(), anchor.getDy2(),
                        anchor.getCol1(), anchor.getRow1(), anchor.getCol2(), anchor.getRow2()
                );

                targetDrawing.createPicture(newAnchor, pictureIndex);
            }
        }
    }

    /**
     * Tạo sheet mới từ sheet mẫu với toàn bộ nội dung và định dạng.
     *
     * @param workbook Workbook chứa sheet
     * @param templateSheet Sheet mẫu
     * @param newSheetName Tên sheet mới
     * @return Sheet mới được tạo
     */
    public static XSSFSheet createSheetFromTemplate(XSSFWorkbook workbook, XSSFSheet templateSheet, String newSheetName) {
        XSSFSheet newSheet = workbook.createSheet(newSheetName);
        copySheetFully(templateSheet, newSheet, workbook);
        return newSheet;
    }
}