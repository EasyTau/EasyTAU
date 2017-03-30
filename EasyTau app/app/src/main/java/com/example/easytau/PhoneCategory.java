package com.example.easytau;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by royn on 19/01/2017.
 */
public class PhoneCategory {

    private Map<String,PhoneUnit> units;
    private String name;

    public PhoneCategory() {
        units = new HashMap<String,PhoneUnit>();
    }

    public Map<String,PhoneUnit> getUnits() {
        return units;
    }

    public void setUnits(Map<String,PhoneUnit> units) {
        this.units = units;
    }

    public void addUnit(String unitStr, PhoneUnit unit) {
        this.units.put(unitStr,unit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
