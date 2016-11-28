package com.example.goldenberg.carroconectado.GPS;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.goldenberg.carroconectado.HttpRequest.GetSpeedLimit;

/**
 * Created by Goldenberg on 11/11/16.
 */
public class GPS_Service extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        System.out.println("Entrou no service");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String velOBD = intent.getStringExtra("velOBD");
                Log.v("Bluetooth",velOBD);
                String lat, lon, lat1, lon1;
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
                lat1 = String.valueOf(location.getLatitude() + 0.0002);
                lon1 = String.valueOf(location.getLongitude() + 0.0002);

                System.out.println("Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude() + " Vel: " + location.getSpeed());
                new GetSpeedLimit().execute(lon, lat, lon1, lat1, velOBD,String.valueOf(location.getTime()));


                //noinspection MissingPermission
                locationManager.removeUpdates(locationListener);
                stopSelf();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null)
            //noinspection MissingPermission
            locationManager.removeUpdates(locationListener);
    }
}
