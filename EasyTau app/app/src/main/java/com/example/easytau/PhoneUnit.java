package com.example.easytau;

import java.util.ArrayList;

/**
 * Created by royn on 19/01/2017.
 */
public class PhoneUnit {
    private ArrayList<PhoneNumber> phoneNumbers;
    private String name;

    public PhoneUnit()
    {
        phoneNumbers = new ArrayList<PhoneNumber>();
    }

    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumbers.add(phoneNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
