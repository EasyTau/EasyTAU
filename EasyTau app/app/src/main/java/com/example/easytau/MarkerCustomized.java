package com.example.easytau;
import com.google.android.gms.maps.model.LatLng;
/**
 * Created by chen on 3/14/2017.
 */


public class MarkerCustomized {
    private String name;
    private String type;
    private LatLng position;
    private String info;
    private String url;
    private String logo;
    private String coursesNav;


    public MarkerCustomized(String name, String type, LatLng position, String info, String url, String logo){
        this.name = name;
        this.type = type;
        this.position = position;
        this.info = info;
        this.url = url;
        this.logo = logo;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCoursesNav() {
        return coursesNav;
    }

    public void setCoursesNav(String coursesNav) {
        this.coursesNav = coursesNav;
    }
}
