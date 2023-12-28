package com.training.myfapplication;

public class Item {
    private String name;
    private String value;

    private String percent;

    public Item(String name, String value, String percent) {
        this.name = name;
        this.value = value;
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getPercent() {
        return percent;
    }
}