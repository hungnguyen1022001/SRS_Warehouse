package com.hungnguyen.srs_warehouse.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.image.BufferedImage;

public class BarcodeUtils {

    // 🔹 Tạo Barcode với kích thước 238x94
    public static BufferedImage generateBarcode(String text) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 238, 94);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException("Lỗi tạo Barcode", e);
        }
    }

    // 🔹 Tạo QR Code với kích thước 269x265
    public static BufferedImage generateQRCode(String text) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 269, 265);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException("Lỗi tạo QR Code", e);
        }
    }
}
