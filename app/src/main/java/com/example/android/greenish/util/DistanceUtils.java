package com.example.android.greenish.util;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class DistanceUtils {

    public static final String API_KEY = "";
    private static float[] results;
    private static final String TAG = "log_trace";
    /**
     * There are several ways to calculate distance between two points,
     * You can 1. use google API services, Like (Direction API or Distance Matrix API)
     *  2. or, use com.google.maps.android.SphericalUtil( : LatLng)
     *  Android Location.distanceBetween( : double)
     *  3. You can calculated by yourself using vincenty formula or haversine formula.
     * */
    public static double getDistanceInMetersUsingAndroidLibrary (double srcLat, double srcLng, double destLat, double destLng) throws
            IllegalArgumentException {

        Log.d(TAG, "getDistanceInMeters: request reached ! ");

        results = new float[3];
        // Computes the approximate distance in meters between two locations.
        Location.distanceBetween(srcLat, srcLng, destLat, destLng, results);
        if (results == null || results.length < 1) {
            // throw an exception
            Log.d(TAG, "getDistanceInMeters: Null result");
            throw new IllegalArgumentException("Invalid value passed to DistanceUtil");
        } else {
            // The computed distance is stored in results[0].
            Log.d(TAG, "getDistanceInMeters: distance computed ");
            return results[0];
        }
    }

    public static double getDistanceInMetersUsingGoogleUtil (double srcLat, double srcLng, double destLat, double destLng) {

        Log.d(TAG, "getDistanceInMeters: request reached ! ");
        // Computes the approximate distance in meters between two locations.

        return com.google.maps.android.SphericalUtil.computeDistanceBetween(new LatLng(srcLat, srcLng),
                new LatLng(destLat, destLng));
    }

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.parseInt(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.parseInt(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
