package com.hungnguyen.srs_warehouse.service.job;

import com.hungnguyen.srs_warehouse.service.OrderDispatchService;
import com.hungnguyen.srs_warehouse.dto.BaseResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDispatchJob {

    private final OrderDispatchService orderDispatchService;

    @Scheduled(fixedRate = 600000)
    public void runBatchJob() {
        BaseResponseDTO<String> response = orderDispatchService.processOrders("username1", true);
        log.info("✅ Batch Job hoan thanh: {}", response.getMessage());
    }
}

