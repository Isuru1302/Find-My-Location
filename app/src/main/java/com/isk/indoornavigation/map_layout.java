package com.isk.indoornavigation;


import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.location.Location;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import com.moagrius.tileview.Tile;
import com.moagrius.tileview.TileView;
import com.moagrius.tileview.io.StreamProviderHttp;
import com.moagrius.tileview.plugins.CoordinatePlugin;
import com.moagrius.tileview.plugins.HotSpotPlugin;


import com.moagrius.tileview.plugins.PathPlugin;

import java.text.DateFormat;


import java.util.Date;


public class map_layout extends AppCompatActivity implements TileView.TileDecodeErrorListener {

    TileView tileView;

    int x = 0, y = 0;
    LinearLayout mainLayout;
    Button StartBtn;
    int flag = 0;


    int mLayoutHeight;
    int mLayoutWidth;
    double mPixelWidth;
    double mPixelHeight;
    double mScale;
    int height, width;




    public static final double NORTH = 7.17237710952758;
    public static final double WEST = 79.98331310000000;
    public static final double SOUTH = 7.17240371000000;
    public static final double EAST = 79.98359084129333;



    //UserLocation
    private static final String TAG = map_layout.class.getSimpleName();
    private String mLastUpdateTime;
    private static final long update_Time = 100;
    private static final long update_Interval = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates = true;


    ImageView prevMarker;
    ImageView userM;

    int[][] mapGrid;
    private int[] point = new int[2];

    private double[] pointCordinates = new double[2];

    private double[] user = new double[2];


    private boolean mIsRestoring;
    Bitmap bitmap;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        tileView = findViewById(R.id.tileview);
        mIsRestoring = savedInstanceState != null;
        tileView.addReadyListener(this::frameToCenter);
        frameToCenterOnReady();
        getSupportActionBar().hide();

        tileViewInfo();



        mapGrid = new int[1000][1000];


        new TileView.Builder(tileView)

                .setSize(16384, 13056)
                .setStreamProvider(new StreamProviderHttp())
                .setDiskCachePolicy(TileView.DiskCachePolicy.CACHE_ALL)
                .defineZoomLevel("http://52.221.180.94/tiles1/phi-1000000-%1$d_%2$d.jpg")
                .defineZoomLevel(1, "http://52.221.180.94/tiles1/phi-500000-%1$d_%2$d.jpg")
                .defineZoomLevel(2, "http://52.221.180.94/tiles1/phi-250000-%1$d_%2$d.jpg")
                .installPlugin(new addMarker(this))
                .installPlugin(new CoordinatePlugin(WEST, NORTH, EAST, SOUTH))
                .installPlugin(new HotSpotPlugin())
                .installPlugin(new PathPlugin())
                .addTouchListener(this::onReady)
                .build();
        UserLocation();


        TextView dis = findViewById(R.id.Display1);
        dis.setText(String.valueOf(user[1]));

        GpsUserLocation();
        startLocationUpdates();

        StartBtn = findViewById(R.id.startBtn);
        StartBtn.setOnClickListener(view -> {

            if (flag == 0) {
                StartBtn.setText("Stop");
                StartBtn.setBackgroundResource(R.drawable.stopbtn);
                flag = 1;
                findPath();




            } else {
                StartBtn.setText("Start");
                StartBtn.setBackgroundResource(R.drawable.startbtn);
                flag = 0;
            }

        });

    }


    @Override
    public void onTileDecodeError(Tile tile, Exception e) {
        tileView.retryTileDecode(tile);
    }


    public void tileViewInfo() {

        mainLayout = findViewById(R.id.mainLayout);
        mScale = tileView.getScale();
        mPixelWidth = tileView.getUnscaledContentWidth();
        mPixelHeight = tileView.getUnscaledContentHeight();


        mLayoutHeight = mainLayout.getHeight();
        mLayoutWidth = mainLayout.getWidth();

        bitmap = (Bitmap) BitmapFactory.decodeResource(getResources(), R.drawable.downsample);

        height = bitmap.getHeight();
        width = bitmap.getWidth();
    }



    private void UserLocation() {

        tileViewInfo();
        Coorinates co = new Coorinates();
        int x = co.longitudeToUnscaledX(user[1], EAST, WEST, mPixelWidth);
        int y = co.latitudeToUnscaledY(user[0], SOUTH, NORTH, mPixelHeight);

        userMark(x, y, R.drawable.usermark);


    }

    private boolean mShouldFrameToCenterOnReady;

    public void frameToCenterOnReady() {
        mShouldFrameToCenterOnReady = true;
    }


    public void frameToCenter(TileView tileView) {
        if (mShouldFrameToCenterOnReady) {
            tileView.post(() -> tileView.scrollTo(
                    tileView.getContentWidth() / 2 - tileView.getWidth() / 2,
                    tileView.getContentHeight() / 2 - tileView.getHeight() / 2
            ));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            TileView tileView = findViewById(R.id.tileview);
            tileView.destroy();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onReady(MotionEvent event) {

        tileView.setOnTouchListener((view, motionEvent) -> {
            tileViewInfo();


            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                x = (int) ((view.getScrollX() + motionEvent.getX()) / mScale);
                y = (int) ((view.getScrollY() + motionEvent.getY()) / mScale);


                Coorinates coordinates = new Coorinates();


                double x1 = coordinates.xToLongitude(x, mScale, EAST, WEST, mPixelWidth);
                double y1 = coordinates.yToLatitude(y, mScale, SOUTH, NORTH, mPixelHeight);


                point[1] = x;
                point[0] = y;
                pointCordinates[1] = x1;
                pointCordinates[0] = y1;
                setMark(point[1], point[0], R.drawable.marker);
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            }

            return false;

        });
    }


    private void setMark(int x, int y, int mark) {

        addMarker addMarkerplugin = tileView.getPlugin(addMarker.class);

        if (prevMarker != null) {
            addMarkerplugin.removeMarker(prevMarker);
        }


        ImageView marker = new ImageView(this);
        marker.setImageResource(mark);
        prevMarker = marker;
        addMarkerplugin.addMarker(marker, x, y, -0.5f, -1f, 0, 0);
    }

    private void userMark(int x, int y, int userMark) {

        addMarker addMarkerplugin = tileView.getPlugin(addMarker.class);


        if (userM != null) {
            addMarkerplugin.removeMarker(userM);
        }

        ImageView usermarker = new ImageView(this);
        usermarker.setImageResource(userMark);
        userM = usermarker;
        addMarkerplugin.addMarker(usermarker, x, y, -0.5f, -1f, 0, 0);

    }


    //Track User
    private void GpsUserLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateGpsUserLocation();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(update_Time);
        mLocationRequest.setFastestInterval(update_Interval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    private void updateGpsUserLocation() {
        if (mCurrentLocation != null) {

            CoordinatePlugin coordinatePlugin = tileView.getPlugin(CoordinatePlugin.class);
            tileViewInfo();
            Coorinates co = new Coorinates();
//            int x = co.longitudeToUnscaledX(mCurrentLocation.getLongitude(), EAST, WEST, mPixelWidth);
//            int y = co.latitudeToUnscaledY(mCurrentLocation.getLatitude(), SOUTH, NORTH, mPixelHeight);

            int x = coordinatePlugin.longitudeToUnscaledX(mCurrentLocation.getLongitude());
            int y = coordinatePlugin.latitudeToUnscaledY(mCurrentLocation.getLatitude());
            userMark(x, y, R.drawable.usermark);
            user[0] = y;
            user[1] = x;

            TextView display = findViewById(R.id.Display1);
            display.setText("0 m");


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {

                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());

                    updateGpsUserLocation();
                })
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {

                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(map_layout.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            Log.e(TAG, errorMessage);

                            Toast.makeText(map_layout.this, errorMessage, Toast.LENGTH_LONG).show();
                    }

                    updateGpsUserLocation();
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();

        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateGpsUserLocation();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(map_layout.this,MainActivity.class);
        startActivity(back);
    }






    private void findPath(){

        Pathfinder.initialize(new Settings() {
            @Override
            public int[][] getGrid() {
                return mapGrid;
            }

            @Override
            public SparseArray<Float> setTravellingCostRules() {
                SparseArray<Float> travellingCostRules = new SparseArray<>();

                // By default Nodes have a travelling factor of 1, if not specified.

                return travellingCostRules;
            }

            @Override
            public boolean isNodeBlocked(int x, int y) {
                return mapGrid[x][y] == 1 ;
            }
        });

        Pathfinder pathfinder = new Pathfinder();
         pathfinder.Path(0, 0, 999, 999,tileView);



    }

}


