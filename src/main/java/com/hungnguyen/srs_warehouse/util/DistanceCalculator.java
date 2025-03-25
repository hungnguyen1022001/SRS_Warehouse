package com.hungnguyen.srs_warehouse.util;

/**
 * Utility class để tính khoảng cách giữa hai điểm dựa trên vĩ độ và kinh độ.
 */
public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");
    }

    /**
     * Tính khoảng cách giữa hai điểm trên Trái Đất sử dụng công thức Haversine.
     *
     * @param lat1 Vĩ độ điểm 1
     * @param lon1 Kinh độ điểm 1
     * @param lat2 Vĩ độ điểm 2
     * @param lon2 Kinh độ điểm 2
     * @return Khoảng cách tính bằng kilomet (km)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);

        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
