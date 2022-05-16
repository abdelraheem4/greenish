package com.example.android.greenish.util;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GpsUtils {


    public static final int REQUEST_CHECK_SETTING = 1001;
    private static final LocationRequest mLocationRequest;


    static { // static block is a block which gets executed => once the class is loaded into a JVM.
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // Set the priority of the request.
        mLocationRequest.setInterval(5000); // Set the desired interval for active location updates, in milliseconds.
        mLocationRequest.setFastestInterval(3000); // Explicitly set the fastest interval for location updates, in milliseconds.
    }

    public static void showLocationPrompt(Context context) throws
            IllegalArgumentException {
        // Toast.makeText(context.getApplicationContext(), "static {} = " + str, Toast.LENGTH_LONG).show();
        // Classes of com.google.android.gms.location Package;

        // Construct object to check whether device' GPS is enabled or not.
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); // Whether or not location is required by the calling app in order to continue.

        // Check if user settings applied to our customised-params Location.
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build());

        // Adds a listener that is called when the Task completes.
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    // ApiException: an Exception to be returned by a Task when a call to Google Play services has failed.
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // If: GPS enabled, Then: No Exception thrown.
                    Toast.makeText(context, "GPS is on", Toast.LENGTH_LONG).show();
                } catch (ApiException e) {
                    // In case: GPS is turned off.
                    // Toast.makeText(context, "ApiException thrown", Toast.LENGTH_LONG).show();

                    switch (e.getStatusCode()) { // Indicates the status of the operation.
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Completing the operation requires some form of resolution.
                            // Code to resolve and enable gps.

                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult((Activity) context, REQUEST_CHECK_SETTING);
                            } catch (IntentSender.SendIntentException ex) {
                                // If the resolution intent has been canceled or is no longer able to execute the request.
                                ex.printStackTrace();
                            } catch (ClassCastException ex) {
                                //
                                throw new IllegalArgumentException("showLocationPrompt(), Context object - expected - " +
                                        "\n" + " _ : given " + context.getClass());
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings can't be changed to meet the requirements, no dialog pops up.
                            break;

                    }
                }
            }
        });
    }

    public static LocationRequest getLocationRequest() {
        return mLocationRequest;
    }

}
