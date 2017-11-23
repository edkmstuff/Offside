package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class KeyValue {
    @com.google.gson.annotations.SerializedName("Key")
    private String key;

    @com.google.gson.annotations.SerializedName("Value")
    private String value;

    public KeyValue(String key, String value){
        this.key = key;
        this.value = value;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
