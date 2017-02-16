package com.suyati.mapstrackingcurrentlocationfinal;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suyati.mapstrackingcurrentlocationfinal.constants.SharedPrefConstants;
import com.suyati.mapstrackingcurrentlocationfinal.service.GPSTrackerBackgroundService;
import com.suyati.mapstrackingcurrentlocationfinal.util.SharedPreferenceUtils;
import com.suyati.mapstrackingcurrentlocationfinal.util.Utilities;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GPSTrackerBackgroundService.ImapsValues{

    private static final int REQUEST_PERMISSION = 0;
    private static final Object NOTIFICATION_ID = 102;

    private GPSTrackerBackgroundService mBoundService;
    private GoogleMap mMap;

//    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(checkLocation()) {
//                doBindService();
//            }else {
//                doUnbindService();
//            }
//        }
//    };

//    private ServiceConnection mConnection = new ServiceConnection() {
//        public void onServiceConnected(ComponentName className, IBinder service) {
//            // This is called when the connection with the service has been
//            // established, giving us the service object we can use to
//            // interact with the service.  Because we have bound to a explicit
//            // service that we know is running in our own process, we can
//            // cast its IBinder to a concrete class and directly access it.
//            mBoundService = ((GPSTrackerBackgroundService.LocalGPSclass)service).getService();
//
//            // Tell the user about this for our demo.
//            Toast.makeText(MainActivity.this, "Service connected",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            // This is called when the connection with the service has been
//            // unexpectedly disconnected -- that is, its process crashed.
//            // Because it is running in our same process, we should never
//            // see this happen.
//            mBoundService = null;
//            Toast.makeText(MainActivity.this, "Service disconnected",
//                    Toast.LENGTH_SHORT).show();
//        }
//    };
//    private boolean mIsBound = false;
//    private LocationManager mLocationManager;

//    void doBindService() {
//        // Establish a connection with the service.  We use an explicit
//        // class name because we want a specific service implementation that
//        // we know will be running in our own process (and thus won't be
//        // supporting component replacement by other applications).
//        if(mIsBound == false) {
//            bindService(new Intent(MainActivity.this,
//                    GPSTrackerBackgroundService.class), mConnection, Context.BIND_AUTO_CREATE);
//            mIsBound = true;
//        }
//    }
//
//    void doUnbindService() {
//        MapsVariableSingleton mapsVariableSingleton = MapsVariableSingleton.getInstance();
//        mapsVariableSingleton.setMaps(mMap);
//        if (mIsBound) {
//            // Detach our existing connection.
//            unbindService(mConnection);
//            mIsBound = false;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.stop:
//                doUnbindService();
                stopService();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void stopService() {
        SharedPreferenceUtils.setSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,true,this);
//        doUnbindService();
        stopService(new Intent(MainActivity.this.getApplicationContext(),GPSTrackerBackgroundService.class));
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        doUnbindService();
//        unregisterReceiver(mBroadcastReceiver);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Utilities utilities = new Utilities(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
            }else {
//                gps calling method
                if(utilities.checkLocation())
//                    doBindService();
                startService();
            }
        }else{
            if(utilities.checkLocation())
//                doBindService();
                startService();
        }
        utilities = null;
    }

    private void startService() {
        SharedPreferenceUtils.setSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,false,this);
        stopService(new Intent(MainActivity.this.getApplicationContext(),GPSTrackerBackgroundService.class));
//        doBindService();
        Intent serviceIntent = new Intent(MainActivity.this.getApplicationContext(),GPSTrackerBackgroundService.class);
        serviceIntent.setFlags(Service.START_STICKY|Service.START_CONTINUATION_MASK);
        startService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean request_result = true;
        if (requestCode == REQUEST_PERMISSION) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale( permission );
                    }
                    if (! showRationale) {
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
//                        showRationale(permission, "Please provide access");
                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {

                    }/* possibly check more permissions...*/
                    request_result = false;
                }
            }
        }
        if(request_result){
//            calling GPS code;
            Utilities utilities = new Utilities(this);
            if(utilities.checkLocation())
//                doBindService();
                startService();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        MapsVariableSingleton mapsVariableSingleton = MapsVariableSingleton.getInstance();
        mapsVariableSingleton.setMaps(mMap);
    }

    @Override
    public void updateMap(LatLng latlng) {
        if(mMap != null){
            AddMarkertoMap(latlng);
        }else {
            MapsVariableSingleton mapsVariableSingleton = MapsVariableSingleton.getInstance();

            mMap = mapsVariableSingleton.getMaps();
                if(mMap != null){
                    AddMarkertoMap(latlng);
                }
        }
    }

    private void AddMarkertoMap(LatLng latlng){
        // Add a marker in Sydney and move the camera
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latlng).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18.0f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,17.0f));
    }
}
