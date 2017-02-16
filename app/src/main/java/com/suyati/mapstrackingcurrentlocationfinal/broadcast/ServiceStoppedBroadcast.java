package com.suyati.mapstrackingcurrentlocationfinal.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.suyati.mapstrackingcurrentlocationfinal.service.GPSTrackerBackgroundService;

/**
 * Created by suyati on 2/16/17.
 */

public class ServiceStoppedBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onReceive");
        Toast.makeText(context,"Service Stopped",Toast.LENGTH_SHORT).show();
    }
}
