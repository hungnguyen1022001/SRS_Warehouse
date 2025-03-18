package com.hungnguyen.srs_warehouse.constant;

public enum OrderStatus {
    NEW(0, "Đơn hàng mới"),
    STORED(1, "Lưu kho"),
    DELIVERED_SUCCESS(2, "Giao hàng thành công"),
    DELIVERED_FAILED(3, "Giao hàng thất bại"),
    RETURNED(4, "Hoàn hàng");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status.getDescription();
            }
        }
        return "Không xác định";
    }
}
