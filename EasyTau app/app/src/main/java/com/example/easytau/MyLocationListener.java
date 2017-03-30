package com.example.easytau;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by shir on 19/03/2017.
 */

public class MyLocationListener implements LocationListener {
    private Location location;


    @Override
    public void onLocationChanged(Location loc) {
        location = new Location(loc);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public Location getLocation() {
        return location;
    }
}
