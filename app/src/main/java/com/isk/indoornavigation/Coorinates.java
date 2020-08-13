package com.isk.indoornavigation;


public class Coorinates {




//    public int longitudeToX(double longitude) {
//        return (int) (longitudeToUnscaledX(longitude) * mScale);
//    }
//
    public int longitudeToUnscaledX(double longitude,double EAST, double WEST,double mPixelWidth) {

            double mDistanceLongitude = EAST - WEST;
            double factor = (longitude - WEST) / mDistanceLongitude;
            return (int)(mPixelWidth * factor);
    }
//
//    public int latitudeToY(double latitude) {
//        return (int) (latitudeToUnscaledY(latitude) * mScale);
//    }
//
    public int latitudeToUnscaledY(double latitude,double SOUTH, double NORTH,double mPixelHeight) {
        double mDistanceLatitude = SOUTH - NORTH;
        double factor= (latitude - NORTH) / mDistanceLatitude;
        return (int)(mPixelHeight * factor);
    }

    public double xToLongitude(int x,double mScale,double EAST, double WEST,double mPixelWidth) {
        double mDistanceLongitude = EAST - WEST;
        return WEST + (x / mScale) * mDistanceLongitude / mPixelWidth;
    }

    public double yToLatitude(int y,double mScale,double SOUTH, double NORTH,double mPixelHeight) {
        double mDistanceLatitude = SOUTH - NORTH;
        return NORTH + (y / mScale) * mDistanceLatitude / mPixelHeight;
   }

}
