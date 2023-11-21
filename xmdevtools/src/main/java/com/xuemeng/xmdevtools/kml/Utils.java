package com.xuemeng.xmdevtools.kml;


import com.xuemeng.xmdevtools.kml.models.Point;

import java.util.List;

/**
 * Created by Edvinas on 2016-02-09.
 */
public class Utils {

    /**
     * Creates string from coordinates array.
     * If figure type is polygon and first and last coordinates are not equal - adds first coordinate to the end to close shape.
     *
     * @param coordinatesList Coordinates array
     * @param figureType      Figure type
     * @return Coordinates string
     */
    public static String createCoordinatesString(List<Point> coordinatesList, C.Type figureType) {
        if (coordinatesList.size() <= 0) return "";
        String coordinates = "";

        for (Point coordinate : coordinatesList)
            coordinates += coordinate.toString() + " ";

        if (figureType == C.Type.POLYGON && !coordinatesList.get(0).equals(coordinatesList.get(coordinatesList.size() - 1))) {
            Point firstCoordinate = coordinatesList.get(0);
            coordinates += firstCoordinate.toString() + " ";
        }

        return coordinates;
    }

    /**
     * Converts RGB (ARBG) color to BGR (ABGR).
     * If color is without alpha - makes alpha "FF".
     *
     * @param color RGB/ARGB color string
     * @return Converted color
     */
    public static String RGBtoBGR(String color) {
        if (color.length() == 6) {
            color = "FF" + color;
        }
        if (color.length() == 8) {
            return color.substring(0, 2) + color.substring(6, 8) + color.substring(4, 6) + color.substring(2, 4);
        }
        return color;
    }

    public static boolean checkGpsCoordination(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    /**
     * a
     */
    public final static double a = 6378245.0;
    /**
     * ee
     */
    public final static double ee = 0.00669342162296594323;

    //圆周率 GCJ_02_To_WGS_84
    public final static double pi = 3.14159265358979324;

    /**
     * @param lon 经度
     * @param lat 纬度
     * @return
     * @Description WGS84 to 火星坐标系 (GCJ-02)
     */
    public static double[] wgs84_To_Gcj02(double lon, double lat) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLon, mgLat};
    }

    /**
     * @param lon
     * @param lat
     * @return
     * @Description 火星坐标系 (GCJ-02) to WGS84
     */
    public static double[] gcj02_To_Wgs84(double lon, double lat) {
        double[] gps = transform(lat, lon);
        double lontitude = lon * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new double[]{lontitude, latitude};
    }

    /**
     * @param gg_lon
     * @param gg_lat
     * @return
     * @Description 火星坐标系 (GCJ-02) to 百度坐标系 (BD-09)
     */
    public static double[] gcj02_To_Bd09(double gg_lon, double gg_lat) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new double[]{bd_lon, bd_lat};
    }

    /**
     * @param bd_lon
     * @param bd_lat
     * @return
     * @Description 百度坐标系 (BD-09) to 火星坐标系 (GCJ-02)
     */
    public static double[] bd09_To_Gcj02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new double[]{gg_lon, gg_lat};
    }

    /**
     * @param bd_lat
     * @param bd_lon
     * @return
     * @Description 百度坐标系 (BD-09) to WGS84
     */
    public static double[] bd09_To_Wgs84(double bd_lon, double bd_lat) {

        double[] gcj02 = bd09_To_Gcj02(bd_lon, bd_lat);
        double[] map84 = gcj02_To_Wgs84(gcj02[0], gcj02[1]);
        return map84;

    }

    /**
     * @param lat
     * @param lon
     * @return
     * @Description 判断是否在中国范围内
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    /**
     * @param lat
     * @param lon
     * @return
     * @Description transform
     */
    private static double[] transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat, lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }

    /**
     * @param x
     * @param y
     * @return
     * @Description transformLat
     */
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * @param x
     * @param y
     * @return
     * @Description transformLon
     */
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * GPS经纬度转换为 度（3114.1717,12122.1067 → 121.37300779，31.23436014）
     *
     * @param dms  坐标
     * @param type 坐标类型  E/N
     * @return String 解析后的经纬度
     */
    public static String gpsToWgs84(String dms, String type) {
        if (dms == null || dms.equals("")) {
            return "0.0";
        }
        double result = 0.0D;
        String temp = "";

        if (type.equals("E")) {//经度
            String e1 = dms.substring(0, 3);//截取3位数字，经度共3位，最多180度
            //经度是一伦敦为点作南北两极的线为0度,所有往西和往东各180度
            String e2 = dms.substring(3, dms.length());//需要运算的小数

            result = Double.parseDouble(e1);
/*            System.out.println("e2===="+e2);
            System.out.println("===="+Double.parseDouble(e2) / 60.0D);*/
            result += (Double.parseDouble(e2) / 60.0D);
            temp = String.valueOf(result);
            if (temp.length() > 11) {
                temp = e1 + temp.substring(temp.indexOf("."), 11);
            }
        } else if (type.equals("N")) {        //纬度，纬度是以赤道为基准,相当于把地球分两半,两个半球面上的点和平面夹角0~90度
            String n1 = dms.substring(0, 2);//截取2位，纬度共2位，最多90度
            String n2 = dms.substring(2, dms.length());

            result = Double.parseDouble(n1);
/*            System.out.println("n2===="+n2);
            System.out.println("===="+Double.parseDouble(n2) / 60.0D);*/
            result += Double.parseDouble(n2) / 60.0D;
            temp = String.valueOf(result);
            if (temp.length() > 10) {
                temp = n1 + temp.substring(temp.indexOf("."), 10);
            }
        }
        return temp;
    }


    /**
     * @param longitude 经度
     * @param latitude  纬度
     * @return double[] x y
     * @Description WGS84 to 高斯投影(6度分带)
     */
    public static double[] wgs84_To_Gauss6(double longitude, double latitude) {
        int ProjNo = 0;
        int ZoneWide; // //带宽
        double[] output = new double[2];
        double longitude1, latitude1, longitude0, X0, Y0, xval, yval;
        double a, f, e2, ee, NN, T, C, A, M, iPI;
        iPI = 0.0174532925199433; // //3.1415926535898/180.0;
        ZoneWide = 6; //6度带宽
        a = 6378137.0;
        f = 1.0 / 298.257223563; //WGS84坐标系参数
        //a = 6378245.0;f = 1.0 / 298.3; // 54年北京坐标系参数
        // //a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ProjNo = (int) (longitude / ZoneWide);
        longitude0 = (double) (ProjNo * ZoneWide + ZoneWide / 2);
        longitude0 = longitude0 * iPI;
        longitude1 = longitude * iPI; // 经度转换为弧度
        latitude1 = latitude * iPI; // 纬度转换为弧度
        e2 = 2 * f - f * f;
        ee = e2 / (1.0 - e2);
        NN = a
                / Math.sqrt(1.0 - e2 * Math.sin(latitude1)
                * Math.sin(latitude1));
        T = Math.tan(latitude1) * Math.tan(latitude1);
        C = ee * Math.cos(latitude1) * Math.cos(latitude1);
        A = (longitude1 - longitude0) * Math.cos(latitude1);
        M = a
                * ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256)
                * latitude1
                - (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2 * e2
                / 1024) * Math.sin(2 * latitude1)
                + (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024)
                * Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072)
                * Math.sin(6 * latitude1));
        // 因为是以赤道为Y轴的，与我们南北为Y轴是相反的，所以xy与高斯投影的标准xy正好相反;
        xval = NN
                * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 14
                * C - 58 * ee)
                * A * A * A * A * A / 120);
        yval = M
                + NN
                * Math.tan(latitude1)
                * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61
                - 58 * T + T * T + 270 * C - 330 * ee)
                * A * A * A * A * A * A / 720);
        X0 = 1000000L * (ProjNo + 1) + 500000L;
        Y0 = 0;
        xval = xval + X0;
        yval = yval + Y0;
        output[0] = xval;
        output[1] = yval;
        return output;
    }
}
