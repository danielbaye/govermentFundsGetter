package com.training.myfapplication;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static Storage instance;
    private Map<String, Float> map;

    private Storage() {
        map = new HashMap<>();
    }

    public static Storage getInstance() {
        if (instance == null) {
            synchronized (Storage.class) {
                if (instance == null) {
                    instance = new Storage();
                }
            }
        }
        return instance;
    }

    public void addToMap(String key, float value) {
        map.put(key, value);
    }

    public float getValue(String key) {
        return map.getOrDefault(key, 0.0f);
    }
}