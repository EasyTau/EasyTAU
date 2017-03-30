package com.example.easytau;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by royn on 19/01/2017.
 */

public class Faculty {
    private Map<String,School> schools;
    private String name;

    public Faculty() {
        schools = new HashMap<String,School>();
    }

    public Map<String,School> getSchools() {
        return schools;
    }

    public void setSchools(Map<String,School> schools) {
        this.schools = schools;
    }

    public void addSchool(String schoolStr, School school) {
        this.schools.put(schoolStr,school);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
