package com.hungnguyen.srs_warehouse.job;

import com.hungnguyen.srs_warehouse.service.OrderDispatchService;
import com.hungnguyen.srs_warehouse.model.DTO.BaseResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchJob {

    private final OrderDispatchService orderDispatchService;

    @Scheduled(fixedRate = 600000) // Chạy mỗi 20 phút
    public void runBatchJob() {
        log.info("🚀 Batch Job - Dang chay dieu phoi don hang...");
        BaseResponseDTO<String> response = orderDispatchService.processOrders("username1", true);
        log.info("✅ Batch Job hoan thanh: {}", response.getMessage());
    }
}

