package com.suyati.mapstrackingcurrentlocationfinal.broadcast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.suyati.mapstrackingcurrentlocationfinal.MainActivity;
import com.suyati.mapstrackingcurrentlocationfinal.service.GPSTrackerBackgroundService;
import com.suyati.mapstrackingcurrentlocationfinal.constants.SharedPrefConstants;
import com.suyati.mapstrackingcurrentlocationfinal.util.SharedPreferenceUtils;
import com.suyati.mapstrackingcurrentlocationfinal.util.Utilities;

public class GPSBroadcastReceiver extends BroadcastReceiver {
    public GPSBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        if(!SharedPreferenceUtils.getSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,context)) {
            Utilities utilities = new Utilities(context);
            if (utilities.checkLocation()) {
                startService(context);
            }
        }
    }

    private void startService(Context context) {
        SharedPreferenceUtils.setSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,false,context);
        context.stopService(new Intent(context,GPSTrackerBackgroundService.class));
        Intent serviceIntent = new Intent(context,GPSTrackerBackgroundService.class);
        serviceIntent.setFlags(Service.START_NOT_STICKY);
        context.startService(serviceIntent);
    }
}
