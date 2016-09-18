package io.github.imruahmed.opti;

import com.google.android.gms.maps.model.LatLng;

public class PlaceItem {
    String name;
    String address;
    LatLng latLng;
    int duration = -1;
    String id = null;

    @Override
    public String toString() {
        return name+"="+address+"="+latLng.latitude+"="+latLng.longitude+"="+duration+"="+id;
    }
}
