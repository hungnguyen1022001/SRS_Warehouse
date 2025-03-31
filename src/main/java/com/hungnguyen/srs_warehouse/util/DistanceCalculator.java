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
     * @param latitude1  Vĩ độ điểm thứ nhất
     * @param longitude1 Kinh độ điểm thứ nhất
     * @param latitude2  Vĩ độ điểm thứ hai
     * @param longitude2 Kinh độ điểm thứ hai
     * @return Khoảng cách tính bằng kilomet (km)
     */
    public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double deltaLatitude = Math.toRadians(latitude2 - latitude1);
        double deltaLongitude = Math.toRadians(longitude2 - longitude1);

        double haversineFormula = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(deltaLongitude / 2), 2);

        return 2 * EARTH_RADIUS_KM * Math.atan2(Math.sqrt(haversineFormula), Math.sqrt(1 - haversineFormula));
    }
}
