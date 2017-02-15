package com.suyati.mapstrackingcurrentlocationfinal.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.suyati.mapstrackingcurrentlocationfinal.alerts.AlertForBroadcastReceiver;

/**
 * Created by suyati on 2/15/17.
 */

public class Utilities{

    Context mContext;

    public Utilities(Context mContext) {
        this.mContext = mContext;
    }

    public boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    public boolean isLocationEnabled() {
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private void showAlert() {
        Intent alertIntent = new Intent(mContext, AlertForBroadcastReceiver.class);
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(alertIntent);
    }
}
