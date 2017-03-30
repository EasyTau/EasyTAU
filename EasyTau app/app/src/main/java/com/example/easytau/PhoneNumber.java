package com.example.easytau;

/**
 * Created by hanim on 06/01/2017.
 */

public class PhoneNumber {
    private String unit;
    private String innerPhone;
    private String category;
    private String name;
    private String fax;
    private String id;
    private String outerPhone;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInnerPhone() {
        return innerPhone;
    }

    public void setInnerPhone(String innerPhone) {
        this.innerPhone = innerPhone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getOuterPhone() {
        return outerPhone;
    }

    public void setOuterPhone(String phone) {
        this.outerPhone = phone;
    }
}
