package com.suyati.mapstrackingcurrentlocationfinal.service;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.suyati.mapstrackingcurrentlocationfinal.MainActivity;
import com.suyati.mapstrackingcurrentlocationfinal.constants.SharedPrefConstants;
import com.suyati.mapstrackingcurrentlocationfinal.data.MapsContractClass;
import com.suyati.mapstrackingcurrentlocationfinal.util.DateTimeUtility;
import com.suyati.mapstrackingcurrentlocationfinal.util.SharedPreferenceUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class GPSTrackerBackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private static final long UPDATE_INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 30000;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    Location mLocation;
    LocationRequest mLocationRequest;


    ImapsValues iMaps;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
        iMaps.updateMap(latLng);
    }

    public GPSTrackerBackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onCreate");
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
////        throw new UnsupportedOperationException("Not yet implemented");
//        iMaps = new MainActivity();
//        if(mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//
//        if(mGoogleApiClient != null) {
//            if(mGoogleApiClient.isConnected()){
//                mGoogleApiClient.reconnect();
//            }else {
//                mGoogleApiClient.connect();
//            }
//        }
//        return null;
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"OnStartCalled");
        initializeGoogleApiObj();
//        runAsForeground(); //runs the service as foreground with notification icon
//        return START_STICKY; START_STICKY has a bug from KITKAT and above,
        return START_NOT_STICKY;
// for reference http://stackoverflow.com/a/20735519/3409734
    }

    private void initializeGoogleApiObj() {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"initializeGoogleApiObj");

        if(iMaps == null) {
            Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"iMaps is null");
            iMaps = new MainActivity();
        }
        if(mGoogleApiClient == null) {
            Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"mGoogleApiClient is null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if(mGoogleApiClient != null) {
            Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"mGoogleApiClient is not null");
            if(mGoogleApiClient.isConnected()){
                Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"mGoogleApiClient is already connected");
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }else {
                Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"mGoogleApiClient is not connected");
                mGoogleApiClient.connect();
            }
        }
    }


    //    private void runAsForeground(){
//        Intent notificationIntent = new Intent(this, GPSTrackerBackgroundService.class);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
//                notificationIntent,PendingIntent.FLAG_ONE_SHOT);
//
//        Notification notification=new NotificationCompat.Builder(this)
//                .setPriority(1)
//                .setAutoCancel(false)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentText(getString(R.string.is_tracking))
//                .setContentIntent(pendingIntent).build();
//        startForeground(NOTIFICATION_ID, notification);
//
//    }

    @Override
    public void onDestroy() {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onDestroy");
        boolean is_stopped = SharedPreferenceUtils.getSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,this);
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
        if(!is_stopped) {
            mGoogleApiClient = null;
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onTaskRemoved");
        Log.e("FLAGX : ", ServiceInfo.FLAG_STOP_WITH_TASK + "");
        boolean is_stopped = SharedPreferenceUtils.getSharedPrefBoolean(SharedPrefConstants.SERVICE_IS_STOPPED,this);
        if(is_stopped) {
            Intent restartServiceIntent = new Intent(getApplicationContext(),
                    this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(
                    getApplicationContext(), 1, restartServiceIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext()
                    .getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 4000,
                    restartServicePendingIntent);
        }
        super.onTaskRemoved(rootIntent);

//        Swiping the app from the recent tasks list actually kills the operating system process
// that hosts the app. Since your service is running in the same process as your activities,
// this effectively kills the service. It does NOT call onDestroy() on the service.
// It just kills the process. Boom. Dead. Gone. Your service does not crash.
//
//                Since your service returned START_STICKY from the call to onStartCommand(),
// Android recognizes that your service should be restarted and schedules a restart of
// the killed service. However, when your service is restarted it will be in a newly created
// process (you can see onCreate() called in the service), so it will have to start the work all over again.
//
//                Rule #1: Don't ever swipe apps from the recent tasks list ;-)
//        http://stackoverflow.com/a/18618060/3409734
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public boolean onUnbind(Intent intent) {
//        if (mGoogleApiClient != null) {
//            if(mGoogleApiClient.isConnected())
//                mGoogleApiClient.disconnect();
//        }
//        return super.onUnbind(intent);
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"On Connected");
        startLocationUpdates();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            setLatLng(latLng);
        } else {
            Toast.makeText(this, "Waiting for location...", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(GPSTrackerBackgroundService.class.getSimpleName(),"onConnectionFailed:"+connectionResult.getErrorCode()+","+connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"onLocationChanged");
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        insertDataToLatLngTable(location);
        // You can now create a LatLng Object for use with maps
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),msg);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        setLatLng(latLng);
    }

    private void insertDataToLatLngTable(Location location) {

        Calendar myCalendar = Calendar.getInstance();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MapsContractClass.LatLngWithTime.LATITUDE,location.getLatitude());
        contentValues.put(MapsContractClass.LatLngWithTime.LONGITUDE,location.getLongitude());

        SimpleDateFormat simpledatefo = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpletimefo = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date newDate = new Date();
        String expectedDate= simpledatefo.format(newDate);
        String expectedTime= simpletimefo.format(newDate);

        contentValues.put(MapsContractClass.LatLngWithTime.DATE, expectedDate);
        contentValues.put(MapsContractClass.LatLngWithTime.TIME, expectedTime);

        List<Address> addresses = getAddressfromGeoLocation(location);
        putAddressValues(addresses,contentValues);

        Cursor cursor = null;

        try{
            cursor = getContentResolver().query(MapsContractClass.LatLngWithTime.CONTENT_URI,
                    new String[]{MapsContractClass.LatLngWithTime.TIME},
                    null,
                    null,
                    MapsContractClass.LatLngWithTime._ID+" DESC LIMIT 1");
            if(cursor.moveToFirst()){
                Date cursortime = DateTimeUtility.getOnlyTimeHour(cursor.getString(0));
                Date currenttime = DateTimeUtility.getOnlyTimeHour(expectedTime);
                if(cursortime.compareTo(currenttime)<0){
                    contentValues.put(MapsContractClass.LatLngWithTime.IS_AN_HOUR,1);
                }else{
                    contentValues.put(MapsContractClass.LatLngWithTime.IS_AN_HOUR,0);
                }
            }else {
                contentValues.put(MapsContractClass.LatLngWithTime.IS_AN_HOUR,1);
            }
            contentValues.put(MapsContractClass.LatLngWithTime.IS_A_STOP,0);
        }catch (Exception e){
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
        }finally {
            if(cursor != null){
                cursor.close();
                cursor = null;
            }
        }
        getContentResolver().insert(MapsContractClass.LatLngWithTime.CONTENT_URI,contentValues);
    }

    private void putAddressValues(List<Address> addresses, ContentValues contentValues) {
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present then only, check with max available address lines by getMaxAddressLineIndex()
        contentValues.put(MapsContractClass.LatLngWithTime.ADDRESS,address);

        String city = addresses.get(0).getLocality();
        contentValues.put(MapsContractClass.LatLngWithTime.CITY,city);

        String state = addresses.get(0).getAdminArea();
        contentValues.put(MapsContractClass.LatLngWithTime.STATE,state);

        String country = addresses.get(0).getCountryName();
        contentValues.put(MapsContractClass.LatLngWithTime.COUNTRY,country);

        String postalCode = addresses.get(0).getPostalCode();
        contentValues.put(MapsContractClass.LatLngWithTime.POSTAL_CODE,postalCode);

        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        contentValues.put(MapsContractClass.LatLngWithTime.KNOWN_NAME,knownName);
    }

    private List<Address> getAddressfromGeoLocation(Location location) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    //    private void ensureServiceStaysRunning() {
//        // KitKat appears to have (in some cases) forgotten how to honor START_STICKY
//        // and if the service is killed, it doesn't restart.  On an emulator & AOSP device, it restarts...
//        // on my CM device, it does not - WTF?  So, we'll make sure it gets back
//        // up and running in a minimum of 20 minutes.  We reset our timer on a handler every
//        // 2 minutes...but since the handler runs on uptime vs. the alarm which is on realtime,
//        // it is entirely possible that the alarm doesn't get reset.  So - we make it a noop,
//        // but this will still count against the app as a wakelock when it triggers.  Oh well,
//        // it should never cause a device wakeup.  We're also at SDK 19 preferred, so the alarm
//        // mgr set algorithm is better on memory consumption which is good.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//        {
//            // A restart intent - this never changes...
//            final int restartAlarmInterval = 20*60*1000;
//            final int resetAlarmTimer = 2*60*1000;
//            final Intent restartIntent = new Intent(this, GPSTrackerBackgroundService.class);
//            restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
//            final AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//            Handler restartServiceHandler = new Handler()
//            {
//                @Override
//                public void handleMessage(Message msg) {
//                    // Create a pending intent
//                    PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
//                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
//                    sendEmptyMessageDelayed(0, resetAlarmTimer);
//                }
//            };
//            restartServiceHandler.sendEmptyMessageDelayed(0, 0);
//        }
//    }
    protected void startLocationUpdates() {
        Log.d(GPSTrackerBackgroundService.class.getSimpleName(),"Start Location Updates");
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    public interface ImapsValues{
        void updateMap(LatLng mMap);
    }

//    public class LocalGPSclass {
//        public GPSTrackerBackgroundService getService() {
//            return GPSTrackerBackgroundService.this;
//        }
//    }
}
