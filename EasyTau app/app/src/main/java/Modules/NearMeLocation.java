package Modules;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by shany on 11/01/2017.
 */

public class NearMeLocation {
    private String name;
    private String type;
    private LatLng address;
    private float distance;
    private int time;

    public NearMeLocation(String name, String type, LatLng address )
    {
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public LatLng getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setDistance(float dis) {
        this.distance = dis;
    }

    public void setTime(int time){
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public float getDistance() {
        return distance;
    }
}
