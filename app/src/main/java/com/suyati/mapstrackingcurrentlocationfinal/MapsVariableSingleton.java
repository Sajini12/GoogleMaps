package com.suyati.mapstrackingcurrentlocationfinal;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.suyati.mapstrackingcurrentlocationfinal.util.Utilities;

/**
 * Created by suyati on 2/14/17.
 */

public class MapsVariableSingleton {

    private static GoogleMap maps;

    public static MapsVariableSingleton getInstance(){
        return new MapsVariableSingleton();
    }

    public GoogleMap getMaps() {
        return maps;
    }

    public void setMaps(GoogleMap maps) {
        this.maps = maps;
    }
}
