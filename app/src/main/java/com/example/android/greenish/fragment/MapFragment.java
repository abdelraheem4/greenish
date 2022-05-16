package com.example.android.greenish.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.android.greenish.MarkerInfo;
import com.example.android.greenish.OnPlantTreeListener;
import com.example.android.greenish.R;
import com.example.android.greenish.dialog.AddTreeDialog;
import com.example.android.greenish.dialog.MarkerInfoDialog;
import com.example.android.greenish.util.DistanceUtils;
import com.example.android.greenish.util.GpsUtils;
import com.example.android.greenish.util.ZoomLevel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapFragment extends Fragment  implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    // constants
    private static final String TAG = "log_trace";
    private static final int GEOFENCE_RADIUS = 10;
    private static final int[] vectors = {R.drawable.ic_tree_fully_watered, R.drawable.ic_tree_semi_watered,
            R.drawable.ic_tree_dried};

    // vars
    private Context context;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Circle mCircle;
    private Marker mMarker;
    private Location userLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<Marker> markers = new ArrayList<>();

    /**
     * for testing purposes
     * public LatLng(@north-south double latitude, @east-west double longitude)
     */
    private LatLng myHome = new LatLng(32.0157842, 36.0417964);


    /**
     *
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: - triggered");
            userLocation = locationResult.getLastLocation();
            LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            addCircle(latLng, GEOFENCE_RADIUS);
            MarkerInfo markerInfo = new MarkerInfo();
            markerInfo.setLatLng(latLng);
            addMarker(context, markerInfo, R.drawable.ic_user);
            detectIfMarkerWithinBoundary(mCircle);

        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability: triggered");
        }
    };

    public MapFragment() {

    }


    /**
     * Changing in states of the fragment throughout lifecycle.
     * */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize view
        Log.d(TAG, "onCreateView: triggered");
        View v = inflater.inflate(R.layout.fragment_map, container, false); // Get layout

        // Initialize map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) { // If layout couldn't  find/inflate  ( R.id.map ).
            Log.d(TAG, "onCreateView: mapFragment is null");
            FragmentManager fManager = getParentFragmentManager();
            FragmentTransaction fTransaction = fManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance(); // Creates a map fragment, using default options programmatically.
            fTransaction.replace(R.id.map, mapFragment).commit();
        }

        // Async map
        mapFragment.getMapAsync(this);
        initView(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    /** @param googleMap, the GoogleMap is an object that is received on a onMapReady() event */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // When map is loaded
        this.mMap = googleMap;

        initMap();
        checkSettingsAndStartLocationUpdates();

        mMap.setOnMarkerClickListener(this);
    }

    private void initMap()
    {
        Log.d(TAG, "initMap: triggered");
        // Disable toolBar
        this.mMap.getUiSettings().setMapToolbarEnabled(false);

        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        Log.d(TAG, "initMap: Accessing location permitted");
        // Enable Focus on user location - relocate -
        this.mMap.setMyLocationEnabled(true);
        moveMyLocationButton();

        LatLng latLng1 = new LatLng(32.016116, 36.040753);
        Marker marker1 = this.mMap.addMarker(new MarkerOptions()
                .position(latLng1)
                .title("Home")
                .snippet("lat:"+latLng1.latitude + ", lng:"+latLng1.longitude)
                .icon(getBitmapDescriptorFromVectorDrawable(context, vectors[0])));

        LatLng latLng2 = new LatLng(32.048013, 35.88840);
        Marker marker2 = this.mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .title("Stop Marker")
                .snippet("lat:"+latLng2.latitude + ", lng:"+latLng2.longitude)
                .icon(getBitmapDescriptorFromVectorDrawable(context, vectors[1])));

        LatLng latLng3 = new LatLng(32.015237, 35.868337);
        Marker marker3 = this.mMap.addMarker(new MarkerOptions()
                .position(latLng3)
                .title("uni of Jordan")
                .snippet("lat:"+latLng3.latitude + ", lng:"+latLng3.longitude)
                .icon(getBitmapDescriptorFromVectorDrawable(context, vectors[2])));

        LatLng latLng4 = new LatLng(32.024989, 35.716800);
        Marker marker4 = this.mMap.addMarker(new MarkerOptions()
                .position(latLng4)
                .title("Bau - IT")
                .snippet("lat:"+latLng4.latitude + ", lng:"+latLng4.longitude)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        markers.add(marker1);
        markers.add(marker2);
        markers.add(marker3);
        markers.add(marker4);
    }

    /**
     * reset my position button
     * TODO: To get View of the map you've to ensure first it's MapAsync method been called and implemented.
     */
    private void moveMyLocationButton()
    {
        View mapView = mapFragment.getView();
        try {
            if (mapView != null) { // skip this if the mapView has not been set yet.
                Log.d(TAG, "moveMyLocationButtonButton()");

                // or To move compass button => View view = mapView.findViewWithTag("GoogleMapCompass");
                View view = mapView.findViewWithTag("GoogleMapMyLocationButton");

                // move the location button to the bottom right corner.
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, // width
                        RelativeLayout.LayoutParams.WRAP_CONTENT); // height

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 150);

                view.setLayoutParams(layoutParams);

            } else {
                Log.d(TAG, "moveMyLocationButton: mapView is null !");
            }
        } catch (Exception ex) {
            Log.d(TAG, "moveMyLocationButton() - failed: " + ex.getMessage()); // returns the name of the exception.
            ex.printStackTrace(); // diagnosing.
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        LatLng latLng = marker.getPosition();

        // send Info back to activity
        // or open dialog directly from here ...
        boolean isUserIn = detectIfMarkerWithinBoundary(marker, mCircle);
        MarkerInfoDialog dialog = MarkerInfoDialog.newInstance(
                new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()),
                "High/Normal/Low", "Planter name", isUserIn
        );
        dialog.show(getParentFragmentManager(), null);
        return false;
    }

    /**
     *
     * @param context
     * @param data
     * @param vectorResId
     */
    private void addMarker(Context context, MarkerInfo data, int vectorResId)
    {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(data.getLatitude(), data.getLongitude()))
                .title(data.getTitle())
                .snippet(data.getSnippet())
                .icon(getBitmapDescriptorFromVectorDrawable(context, vectorResId))
        );

    }

    /**
     *
     * @param latLng
     * @param radius
     */
    private void addCircle(LatLng latLng, float radius)
    {
        if (mCircle != null) {
            mCircle.remove();
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.argb(255, 255, 0, 0))
                .fillColor(Color.argb(46, 255, 0, 0))
                .strokeWidth(2);

        mCircle = mMap.addCircle(circleOptions);
    }


    /**
     *
     * @param context
     * @param vectorResId
     * @return
     */
    private BitmapDescriptor getBitmapDescriptorFromVectorDrawable(Context context, int vectorResId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } else {
            Log.d(TAG, "getBitmapDescriptorFromVectorDrawable: NullPointerException: drawable is null ... ");
        }

        return BitmapDescriptorFactory.defaultMarker();
    }

    private boolean detectIfMarkerWithinBoundary(Circle circle)
    {
        if (circle == null)
            return false;
        double distance = DistanceUtils.getDistanceInMetersUsingAndroidLibrary(circle.getCenter().latitude, circle.getCenter().longitude,
                myHome.latitude, myHome.longitude);
        if (distance <= circle.getRadius()) {
            circle.setFillColor(Color.argb(50, 0, 255, 0));
            return true;
        } else {
            circle.setFillColor(Color.argb(50, 255, 0, 0));
            return false;
        }
    }

    private boolean detectIfMarkerWithinBoundary(Marker marker, Circle circle)
    {
        if (circle == null)
            return false;
        double distance = DistanceUtils.getDistanceInMetersUsingAndroidLibrary(circle.getCenter().latitude, circle.getCenter().longitude,
                marker.getPosition().latitude, marker.getPosition().longitude);
        if (distance <= circle.getRadius()) {
            circle.setFillColor(Color.argb(50, 0, 255, 0));
            Toast.makeText(context, "_ Inside :: ", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            circle.setFillColor(Color.argb(50, 255, 0, 0));
            Toast.makeText(context, "_ Outside :: ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * The moveCamera method repositions the camera according to the instructions defined in the update.
     * @param data
     * @param zoom
     */
    private void moveCamera (MarkerInfo data, float zoom)
    {
        if (mMap != null) {
            Log.d(TAG, "moveCamera: moving the camera to: lat: " + data.getLatitude() + ", lng: " + data.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(data.getLatitude(), data.getLongitude()), zoom));

        } else {
            Log.d(TAG, "moveCamera: map hasn't been initialized _ !");
        }
    }


    /**
     * Access user location
     */
    private void checkSettingsAndStartLocationUpdates()
    {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(GpsUtils.getLocationRequest()).build();

        SettingsClient client = LocationServices.getSettingsClient(context);

        Task<LocationSettingsResponse> responseTask = client.checkLocationSettings(request);
        responseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // Settings of device are satisfied and we can start location updates.
                startLocationUpdates();
            }
        });

        responseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        if (getActivity() != null) // returns the Activity the fragment is associated with.
                            apiException.startResolutionForResult(getActivity(), GpsUtils.REQUEST_CHECK_SETTING);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            boolean isOk = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!isOk)
                return;
        }
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        mFusedLocationProviderClient.requestLocationUpdates(GpsUtils.getLocationRequest(),
                mLocationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates()
    {
        if (mFusedLocationProviderClient != null)
        {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     *
     *
     * */
    private final OnPlantTreeListener onPlantTreeListener = new OnPlantTreeListener() {
        @Override
        public void onPlantTree(boolean ok, MarkerOptions markerOptions) {
            if (ok)
            {
                Marker marker = MapFragment.this.mMap.addMarker(markerOptions);
                markers.add(marker);
            }
        }
    };

    private void initView(View view)
    {
        FloatingActionButton actionButton = view.findViewById(R.id.addTreeFloatingActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userLocation != null) {

                    // get current location
                    if (latLng[0] == null) {
                        getCurrentLocation();
                        return;
                    }

                    double latitude = latLng[0].latitude; // can be replaced with userLocation.getLatitude();
                    double longitude = latLng[0].longitude; // can be replaced with userLocation.getLongitude();
                    MarkerInfo markerInfo = new MarkerInfo();
                    markerInfo.setLatLng(new LatLng(latitude, longitude));
                    markerInfo.setIcon(vectors[0]);
                    moveCamera(markerInfo, ZoomLevel.BUILDINGS_LEVEL);

                    // popup dialog
                    ArrayList<String> info = geocode(latitude, longitude);
                    AddTreeDialog dialog = AddTreeDialog.newInstance(onPlantTreeListener, info);
                    dialog.show(getParentFragmentManager(), "AddTreeDialog");
                } else {
                    Toast.makeText(context, "Plz, wait a bit", Toast.LENGTH_SHORT).show();
                    checkSettingsAndStartLocationUpdates();
                }
            }
        });
    }

    private ArrayList<String> geocode(double lat, double lng)
    {
        Geocoder geocoder = new Geocoder(context);
        List<Address> list = new ArrayList<>();
        ArrayList<String> addressInfo = new ArrayList<>();
        try {
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (java.io.IOException e) {
            Log.d(TAG, "onComplete: failed to know address from coordinates");
        } finally {
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                addressInfo.add(address.getLatitude()+"");
                addressInfo.add(address.getLongitude()+"");
                addressInfo.add(address.getCountryName());
                addressInfo.add(address.getAdminArea());
                addressInfo.add(address.getSubAdminArea());
                addressInfo.add(address.getThoroughfare());
                addressInfo.add(address.getSubThoroughfare());
            }
        }
        return addressInfo;
    }

    final LatLng[] latLng = new LatLng[1];
    private void getCurrentLocation () {
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }

        try {
            com.google.android.gms.tasks.Task<Location> currentLocation = mFusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, null);

            currentLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = currentLocation.getResult();
                        if (location != null) {
                            latLng[0] = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerInfo markerInfo = new MarkerInfo();
                            markerInfo.setLatLng(latLng[0]);
                            moveCamera(markerInfo, ZoomLevel.BUILDINGS_LEVEL);
                        } else {
                            Toast.makeText(context, "Your GPS is turned off !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "onComplete: failed to get the current location !");
                    }
                }
            });

        } catch (java.lang.SecurityException e) {
            Toast.makeText(context, "getCurrentLocation: SecurityException:\n _require access permissions_",
                    Toast.LENGTH_LONG).show();
        }
    }

}