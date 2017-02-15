package com.suyati.mapstrackingcurrentlocationfinal.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by suyati on 2/15/17.
 */

public class MapsContractClass {

    public static final String PATH_LATLNG_WITH_TIME_MASTER = "LatLng_With_Time";

    //setting authority FOR LATLNG WITH TIME MASTER
    public static final String MAPS_AUTHORITY = "com.suyati.mapstrackingcurrentlocationfinal.maps_authority";
    public static final Uri MAPS_CONTENT_URI = Uri.parse("content://" + MAPS_AUTHORITY);

    public static final class LatLngWithTime implements BaseColumns{
        public static String TABLE_NAME = "LatLng_With_Time";
        public static String LATITUDE = "latitude";
        public static String LONGITUDE = "longitude";
        public static String TIME = "time";
        public static String ADDRESS = "address";
        public static String IS_AN_HOUR = "is_an_hour";
        public static String DATE ="date";

        //setting content uri path
        public static final Uri CONTENT_URI = MAPS_CONTENT_URI.buildUpon().appendPath(PATH_LATLNG_WITH_TIME_MASTER).build();

        //content type
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + MAPS_AUTHORITY + "/" + PATH_LATLNG_WITH_TIME_MASTER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + MAPS_AUTHORITY + "/" + PATH_LATLNG_WITH_TIME_MASTER;

        public static Uri buildLatLngWithTimeMasterUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
