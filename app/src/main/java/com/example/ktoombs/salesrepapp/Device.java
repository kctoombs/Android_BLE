package com.example.ktoombs.salesrepapp;

/**
 * Created by ktoombs on 7/19/2017.
 */

public class Device {
    private String name;
    private String macAddr;

    public Device(String name, String macAddr){
        this.name = name;
        this.macAddr = macAddr;
    }

    public String getName(){
        return this.name;
    }

    public String getMacAddr(){
        return this.macAddr;
    }
}
