package com.example.easytau;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by hanim on 15/01/2017.
 */

public class EventCalendar {
    private String fullDate;
    private String from_date;
    private String jewish_date;
    private String name;
    private String notes;
    private String to_date;
    private String from_h;
    private String to_h;

    public String getFrom_h()
    {
        return from_h;
    }

    public void setFrom_h(String h){
        this.from_h = h;
    }

    public void setTo_h(String h){
        this.to_h = h;
    }

    public String getTo_h()
    {
        return to_h;
    }
    public String getFullDate(){
        return fullDate;
    }

    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String date) {
        this.from_date = date;
    }

    public String getJewish_date() {
        return jewish_date;
    }

    public void setJewish_date(String jewish_date) {
        this.jewish_date = jewish_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public long convertTimestamp() throws ParseException {
        Date date = convertStringToDate(this.from_date) ;
        // Timestamp timestamp = new java.sql.Timestamp(date.getTime());

        return date.getTime();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private Date convertStringToDate(String stringDate) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        Date date = format.parse(stringDate);

        return date;

    }

}
