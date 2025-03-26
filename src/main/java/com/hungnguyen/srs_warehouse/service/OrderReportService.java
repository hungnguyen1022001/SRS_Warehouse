package com.hungnguyen.srs_warehouse.service;

import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import com.hungnguyen.srs_warehouse.repository.OrderRepository;
import com.hungnguyen.srs_warehouse.util.report.ExcelReportUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderReportService {
    private final OrderRepository orderRepository;
    private final MessageSource messageSource;

    @Value("${report.max-warehouses:10}")
    private int maxWarehouses;

    @Value("${report.max-days:15}")
    private int maxDays;

    @Value("${report.max-months:12}")
    private int maxMonths;

    public BaseResponseDTO<byte[]> generateReport(List<String> warehouseIds, LocalDate startDate, LocalDate endDate, boolean isMonthly) throws IOException {
        // Chuyển đổi LocalDate thành LocalDateTime để phù hợp với kiểu dữ liệu trong DB
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Kiểm tra số lượng kho hàng
        if (warehouseIds.size() > maxWarehouses) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("REPORT_MAX_WAREHOUSES", new Object[]{maxWarehouses}, Locale.getDefault()));
        }

        // Kiểm tra khoảng thời gian hợp lệ
        long rangeLimit = isMonthly ? maxMonths : maxDays;
        long actualRange = isMonthly ? ChronoUnit.MONTHS.between(startDate, endDate) : ChronoUnit.DAYS.between(startDate, endDate);
        if (actualRange > rangeLimit) {
            String errorKey = isMonthly ? "REPORT_MONTH_RANGE_EXCEEDED" : "REPORT_DATE_RANGE_EXCEEDED";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(errorKey, new Object[]{rangeLimit}, Locale.getDefault()));
        }

        // Lấy dữ liệu báo cáo từ repository
        List<Object[]> reportData = isMonthly
                ? orderRepository.getOrderStatisticsByMonth(warehouseIds, startDateTime, endDateTime)
                : orderRepository.getOrderStatisticsByDay(warehouseIds, startDateTime, endDateTime);

        // Xuất file báo cáo
        byte[] fileData = ExcelReportUtils.createReportFile(reportData, isMonthly, startDate, endDate);
        return new BaseResponseDTO<>(1, messageSource.getMessage("REPORT_SUCCESS", null, Locale.getDefault()), fileData);
    }

    public String generateFileName() {
        return "Report01_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }
}
