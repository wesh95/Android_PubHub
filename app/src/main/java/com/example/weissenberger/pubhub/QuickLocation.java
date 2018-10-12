package com.example.weissenberger.pubhub;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Ben on 12/15/2017.
 */

public class QuickLocation {
    private static boolean mLocationPermissionGranted;
    private static Location mLastKnownLocation;
    private static FusedLocationProviderClient mFusedLocationProviderClient;




}
