package com.example.easytau;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by royn on 19/01/2017.
 */

public class TotalInfoFromDb {

    //Faculties:

    static private Map<String,Faculty> faculties = new HashMap<String,Faculty>();

    public static Map<String, LatLng> getMapParking() {
        return mapParking;
    }

    public static void setMapParking(Map<String, LatLng> mapParking) {
        TotalInfoFromDb.mapParking = mapParking;
    }
    static public void addMapParking(String name, LatLng coor) {
        mapParking.put(name, coor);
    }

    public static List<ParkingLot> getParkingLotListM() {
        return parkingLotListM;
    }

    public static void setParkingLotListM(List<ParkingLot> parkingLotListM) {
        TotalInfoFromDb.parkingLotListM = parkingLotListM;
    }
    public static void addParkingLotListM(ParkingLot parkingLot){
        parkingLotListM.add(parkingLot);
    }

    static private List<ParkingLot> parkingLotListM = new ArrayList<>();



    static private Map<String,LatLng> mapParking = new HashMap<>();

    static public Map<String,Faculty> getFaculties() {
        return faculties;
    }

    static public void setFaculties(Map<String,Faculty> newFaculties) {
        faculties = newFaculties;
    }

    static public void addFaculty(String facultyStr, Faculty faculty) {
        faculties.put(facultyStr, faculty);
    }

    static public int getFacultiesCount() {
        return faculties.size();
    }

    //Courses

    static private Map<String,ArrayList<Course>> courses = new HashMap<String,ArrayList<Course>>();

    static public Map<String,ArrayList<Course>> getCourses() {
        return courses;
    }

    static public void setCourses(Map<String,ArrayList<Course>> newCourses) {
        courses = newCourses;
    }

    //Phone numbers:

    static private Map<String,PhoneCategory> phoneCategories = new HashMap<String,PhoneCategory>();

    static public Map<String,PhoneCategory> getPhoneCategories() {
        return phoneCategories;
    }

    static public void setPhoneCategories(Map<String,PhoneCategory> newPhoneCategories) {
        phoneCategories = newPhoneCategories;
    }

    static public void addPhoneCategory(String phoneCategoryStr, PhoneCategory phoneCategory) {
        phoneCategories.put(phoneCategoryStr, phoneCategory);
    }

    static public int getPhoneCategoriesCount() {
        return phoneCategories.size();
    }


    //TAU Events:

    static private List<TauEvent> tauEvents = new ArrayList<TauEvent>();

    static public List<TauEvent> getTauEvents() {
        return tauEvents;
    }

    static public void setTauEvents(List<TauEvent> newTauEvents) {
        tauEvents = newTauEvents;
    }

    static public void addTauEvent(TauEvent tauEvent) {
        tauEvents.add(tauEvent);
    }

    static public int getTauEventsCount() {
        return tauEvents.size();
    }

    static private int schoolsCounter;
    static private int coursesCounter;
    static private int phoneNumbersCounter;
    static private int phoneUnitsCounter;

    static public void beginSchoolsCounter() {
        schoolsCounter = 0;
    }

    static public void beginCoursesCounter() {
        coursesCounter = 0;
    }

    static public void beginPhoneNumbersCounter() {
        phoneNumbersCounter = 0;
    }

    static public void beginPhoneUnitsCounter() {
        phoneUnitsCounter = 0;
    }

    static public int getSchoolsCounter() {
        return schoolsCounter;
    }

    static public int getCoursesCounter() {
        return coursesCounter;
    }

    static public int getPhoneNumbersCounter() {
        return phoneNumbersCounter;
    }

    static public int getPhoneUnitsCounter() {
        return phoneUnitsCounter;
    }

    static public void incrementCoursesCounter() {
        coursesCounter++;
    }

    static public void incrementSchoolsCounter() {
        schoolsCounter++;
    }

    static public void incrementPhoneNumbersCounter() {
        phoneNumbersCounter++;
    }

    static public void incrementPhoneUnitsCounter() {
        phoneUnitsCounter++;
    }
}
