package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.model.DTO.orderDetail.OrderLabelDTO;
import com.hungnguyen.srs_warehouse.mapper.OrderLabelMapper;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.util.ExcelLabelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderLabelService {
    private final OrderRepository orderRepository;
    private final OrderLabelMapper orderLabelMapper;
    private final MessageSource messageSource;

    private static final String DOWNLOAD_FOLDER = System.getProperty("user.home") + "/Downloads/";

    public BaseResponseDTO<String> generateLabelExcel(List<String> orderIds) {
        try {
            // Kiểm tra số lượng đơn hàng tối đa
            if (orderIds.size() > 10) {
                return new BaseResponseDTO<>(0,
                        messageSource.getMessage("EXCEL_MAX_ORDERS", null, Locale.getDefault()),
                        null);
            }

            // Lấy danh sách đơn hàng từ DB
            List<OrderLabelDTO> orders = orderRepository.findByOrderIdIn(orderIds)
                    .stream()
                    .map(orderLabelMapper::toDto)
                    .toList();

            // Kiểm tra đơn hàng có tồn tại không
            if (orders.size() != orderIds.size()) {
                return new BaseResponseDTO<>(0,
                        messageSource.getMessage("ORDER_001", null, Locale.getDefault()),
                        null);
            }

            // Xuất file Excel
            byte[] excelFile = ExcelLabelUtils.createLabelFile(orders);

            // Lưu file vào thư mục Downloads
            String filePath = saveToFile(excelFile, generateFileName());

            // Trả về JSON theo chuẩn BaseResponseDTO
            return new BaseResponseDTO<>(1,
                    messageSource.getMessage("SUCCESS", null, Locale.getDefault()),
                    filePath);
        } catch (Exception e) {
            return new BaseResponseDTO<>(0,
                    messageSource.getMessage("SERVER_ERROR", null, Locale.getDefault()) + ": " + e.getMessage(),
                    null);
        }
    }

    private String saveToFile(byte[] data, String fileName) throws IOException {
        File directory = new File(DOWNLOAD_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }

        String filePath = Paths.get(DOWNLOAD_FOLDER, fileName).toString();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
        System.out.println("File saved at: " + filePath);
        return filePath;
    }

    public String generateFileName() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "Labels_" + date + ".xlsx";
    }
}
