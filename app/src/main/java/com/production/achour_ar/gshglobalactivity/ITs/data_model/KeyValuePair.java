package com.production.achour_ar.gshglobalactivity.ITs.data_model;

public class KeyValuePair {

    String Key, Value;

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public KeyValuePair(String key, String value) {

        Key = key;
        Value = value;
    }


}
