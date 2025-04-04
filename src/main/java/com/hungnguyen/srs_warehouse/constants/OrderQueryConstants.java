package com.hungnguyen.srs_warehouse.constants;

public class OrderQueryConstants {


    public static final String SEARCH_ORDERS = """
        SELECT o FROM Order o 
        WHERE (:orderId IS NULL OR o.orderId = :orderId)
        AND (:phone IS NULL OR o.receiver.phone = :phone OR o.supplier.phone = :phone)
        AND (:status IS NULL OR o.status = :status)
        AND (:warehouseId IS NULL OR o.warehouse.warehouseId = :warehouseId)
    """;

    public static final String ORDER_REPORT_BY_DAY = """
        SELECT new com.hungnguyen.srs_warehouse.dto.report.OrderReportByDayDTO(
               w.warehouseId, w.name, o.storedAt, COUNT(o.orderId)) 
        FROM Order o 
        JOIN o.warehouse w 
        WHERE w.warehouseId IN :warehouseIds 
        AND o.storedAt BETWEEN :startDate AND :endDate 
        GROUP BY w.warehouseId, w.name, o.storedAt 
        ORDER BY w.warehouseId, o.storedAt
    """;

    public static final String ORDER_REPORT_BY_MONTH = """
        SELECT new com.hungnguyen.srs_warehouse.dto.report.OrderReportByMonthDTO(
               w.warehouseId, w.name, EXTRACT(YEAR FROM o.storedAt), 
               EXTRACT(MONTH FROM o.storedAt), COUNT(o.id)) 
        FROM Order o 
        JOIN o.warehouse w 
        WHERE w.warehouseId IN :warehouseIds 
        AND o.storedAt BETWEEN :startDate AND :endDate 
        GROUP BY w.warehouseId, w.name, EXTRACT(YEAR FROM o.storedAt), EXTRACT(MONTH FROM o.storedAt) 
        ORDER BY w.warehouseId, EXTRACT(YEAR FROM o.storedAt), EXTRACT(MONTH FROM o.storedAt)
    """;

    public static final String FIND_MAX_ORDER_ID_FOR_TODAY = """
        SELECT MAX(o.orderId) 
        FROM Order o 
        WHERE o.orderId LIKE CONCAT('DH-', :datePart, '-%')
    """;

}
