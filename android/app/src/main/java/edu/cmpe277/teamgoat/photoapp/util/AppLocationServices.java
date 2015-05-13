package edu.cmpe277.teamgoat.photoapp.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import edu.cmpe277.teamgoat.photoapp.PhotoApp;

public class AppLocationServices implements LocationListener {


    private LocationManager locationManager;
    private Location lastKnownLocation;

    public AppLocationServices(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * Starts Location Listener
     */
    public void startLocationListener() {
        boolean locationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(locationEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
        if(gpsLocationEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    public Location getLastKnownLocationFromService() {
        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location lastKnownGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return (isBetterLocation(lastKnownGPSLocation)) ? lastKnownGPSLocation : lastKnownLocation;
        }
        catch (Exception e) {
            PaLog.error(String.format("Error getting last location, Msg: '%s'", e.getMessage()), e);
            return null;
        }
    }


    /**
     * Stops Location Listener
     */
    public void stopLocationListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public Double getLastKnownLocationLat() {
        return lastKnownLocation == null ? null : lastKnownLocation.getLatitude();
    }

    public Double getLastKnownLocationLong() {
        return lastKnownLocation == null ? null : lastKnownLocation.getLongitude();
    }


    private boolean isBetterLocation(Location location) {
        final int TWO_MINUTES = 1000 * 60 * 2;

        if (lastKnownLocation != null) {
            long timeDelta = location.getTime() - lastKnownLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            if (isSignificantlyNewer) {
                return true;
            }
            else if (isSignificantlyOlder) {
                return false;
            }
            else {
                int accuracyDelta = (int) (location.getAccuracy() - lastKnownLocation.getAccuracy());
                boolean isLessAccurate = accuracyDelta > 0;
                boolean isMoreAccurate = accuracyDelta < 0;
                boolean isSignificantlyLessAccurate = accuracyDelta > 200;
                boolean isFromSameProvider = (location.getProvider() == null) ? lastKnownLocation.getProvider() == null : location.equals(lastKnownLocation);


                return (isMoreAccurate) || (isNewer && !isLessAccurate) || (isNewer && !isSignificantlyLessAccurate && isFromSameProvider);
            }
        }
        else {
            return true;
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location)) {
            lastKnownLocation = location;
        }
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
}
