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
    /**
     * Tạo và chèn mã vạch vào sheet Excel trong phạm vi J2:M3
     * @param sheet Sheet trong workbook
     * @param barcode Chuỗi dữ liệu để tạo mã vạch
     * @param workbook Workbook chứa sheet
     */
    public static void insertBarcodeInRange(Sheet sheet, String barcode, XSSFWorkbook workbook) {
        insertImageInRange(sheet, barcode, workbook, BarcodeFormat.CODE_128, 400, 80, 9, 1, 13, 3);
    }

    /**
     * Tạo và chèn mã QR vào sheet Excel trong phạm vi J20:N25
     * @param sheet Sheet trong workbook
     * @param qrData Chuỗi dữ liệu để tạo mã QR
     * @param workbook Workbook chứa sheet
     */
    public static void insertQRCodeInRange(Sheet sheet, String qrData, XSSFWorkbook workbook) {
        insertImageInRange(sheet, qrData, workbook, BarcodeFormat.QR_CODE, 1000, 1000, 11, 19, 13, 25);
    }

    /**
     * Tạo mã vạch/QR code và chèn vào Excel
     * @param sheet Sheet cần chèn hình ảnh
     * @param data Dữ liệu để tạo mã
     * @param workbook Workbook chứa sheet
     * @param format Định dạng mã (BarcodeFormat.CODE_128 hoặc BarcodeFormat.QR_CODE)
     * @param width Chiều rộng ảnh
     * @param height Chiều cao ảnh
     * @param startCol Cột bắt đầu chèn ảnh
     * @param startRow Hàng bắt đầu chèn ảnh
     * @param endCol Cột kết thúc chèn ảnh
     * @param endRow Hàng kết thúc chèn ảnh
     */
    private static void insertImageInRange(Sheet sheet, String data, XSSFWorkbook workbook,
                                           BarcodeFormat format, int width, int height,
                                           int startCol, int startRow, int endCol, int endRow) {
        try {
            // Tạo mã vạch hoặc mã QR
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, format, width, height);

            // Chuyển đổi BitMatrix sang BufferedImage
            BufferedImage image = toBufferedImage(bitMatrix);

            // Chuyển ảnh thành mảng byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            // Chèn ảnh vào workbook
            int pictureIdx = workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();

            // Tạo anchor cho ảnh
            ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
            anchor.setCol1(startCol);
            anchor.setRow1(startRow);
            anchor.setCol2(endCol);
            anchor.setRow2(endRow);

            // Thêm ảnh vào sheet
            drawing.createPicture(anchor, pictureIdx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Chuyển đổi BitMatrix thành BufferedImage
     * @param matrix BitMatrix chứa dữ liệu mã vạch hoặc QR
     * @return BufferedImage tương ứng với BitMatrix
     */
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
