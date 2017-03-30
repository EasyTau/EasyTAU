package com.example.easytau;

import java.util.ArrayList;

/**
 * Created by royn on 19/01/2017.
 */

public class School {
    private ArrayList<String> courses;
    private String name;

    public School()
    {
        courses = new ArrayList<String>();
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }

    public void addCourse(String course) {
        this.courses.add(course);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
