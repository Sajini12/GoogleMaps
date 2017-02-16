package com.suyati.mapstrackingcurrentlocationfinal.alerts;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.suyati.mapstrackingcurrentlocationfinal.R;

/**
 * Created by suyati on 2/15/17.
 */

public class AlertForBroadcastReceiver extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_alert_view);
//        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        int totalWidth = getWindow().getDecorView().getWidth();
        int totalHeight = getWindow().getDecorView().getHeight();
        params.x = 0;
        params.height = 650;
        params.width = totalWidth;
        params.y = totalHeight/10;

        this.getWindow().setAttributes(params);

        getWindow().getDecorView().setVisibility(View.GONE);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.dismiss();
                        AlertForBroadcastReceiver.this.finish();
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        paramDialogInterface.dismiss();
                        AlertForBroadcastReceiver.this.finish();
                    }
                });
        dialog.show();
    }
}
