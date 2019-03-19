package com.example.ridesafedatacollection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import java.util.List;

public class GPSConfig implements android.location.GpsStatus.Listener {
    private static final int gpsMinTime = 500;
    private static final int gpsMinDistance = 0;
    private static LocationManager locationManager = null;
    private static LocationListener locationListener = null;
    private static GPSUpdate gpsCallback = null;
    Context myContext;

    public GPSConfig(Context context) {
        myContext = context;
        GPSConfig.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if (GPSConfig.gpsCallback != null) {
                    GPSConfig.gpsCallback.onGPSUpdate(location);
                }
            }

            @Override
            public void onProviderDisabled(final String provider) {
            }

            @Override
            public void onProviderEnabled(final String provider) {
            }

            @Override
            public void onStatusChanged(final String provider, final int status, final Bundle extras) {
            }
        };
    }

    public GPSUpdate getGPSCallback() {
        return GPSConfig.gpsCallback;
    }


    public void setGPSCallback(final GPSUpdate gpsCallback) {
        GPSConfig.gpsCallback = gpsCallback;
    }

    public void startListening(final Context context) {
        if (GPSConfig.locationManager == null) {
            GPSConfig.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);


        final String bestProvider = GPSConfig.locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        if (bestProvider != null && bestProvider.length() > 0) {
            GPSConfig.locationManager.requestLocationUpdates(bestProvider, GPSConfig.gpsMinTime,
                    GPSConfig.gpsMinDistance, GPSConfig.locationListener);
        } else {
            final List<String> providers = GPSConfig.locationManager.getProviders(true);
            for (final String provider : providers) {
                GPSConfig.locationManager.requestLocationUpdates(provider, GPSConfig.gpsMinTime,
                        GPSConfig.gpsMinDistance, GPSConfig.locationListener);
            }
        }
    }

    public void stopListening() {
        try {
            if (GPSConfig.locationManager != null && GPSConfig.locationListener != null) {
                GPSConfig.locationManager.removeUpdates(GPSConfig.locationListener);
            }
            GPSConfig.locationManager = null;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onGpsStatusChanged(int event) {
        int Satellites = 0;
        int SatellitesInFix = 0;
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) myContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
        Log.i("GPs", "Time to first fix = " + String.valueOf(timetofix));
        for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
            if (sat.usedInFix()) {
                SatellitesInFix++;
            }
            Satellites++;
        }
        Log.i("GPS", String.valueOf(Satellites) + " Used In Last Fix (" + SatellitesInFix + ")");
    }
}