package com.hungnguyen.srs_warehouse.util.label;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class BarcodeFillUtils {

    public static void insertBarcodeInRange(Sheet sheet, String barcode, XSSFWorkbook workbook) {
        insertImageInRange(sheet, barcode, workbook, BarcodeFormat.CODE_128, 400, 80, 9, 1, 13, 3);
    }

    public static void insertQRCodeInRange(Sheet sheet, String qrData, XSSFWorkbook workbook) {
        insertImageInRange(sheet, qrData, workbook, BarcodeFormat.QR_CODE, 1000, 1000, 11, 19, 13, 25);
    }

    private static void insertImageInRange(Sheet sheet, String data, XSSFWorkbook workbook,
                                           BarcodeFormat format, int width, int height,
                                           int startCol, int startRow, int endCol, int endRow) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, format, width, height);
            BufferedImage image = toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            int pictureIdx = workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(startCol);
            anchor.setRow1(startRow);
            anchor.setCol2(endCol);
            anchor.setRow2(endRow);

            drawing.createPicture(anchor, pictureIdx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }
}
