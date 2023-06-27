package com.training.myfapplication;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class InfoHandler {

    private Stack<Map.Entry<String,String>> currentDepartmentCode;
    private String url;
    public InfoHandler(String url) {
        this.url = url;
        currentDepartmentCode = new Stack<>();
        currentDepartmentCode.push( new AbstractMap.SimpleEntry<>("00","הכל"));
    }

    public ArrayList<Map.Entry<String,Float>> getDepartmentsAndValues(){
        infoGetter ig = new infoGetter();
        String s = ig.getQueryResponse(url + "SELECT children FROM budget " +
                "where code " +currentDepartmentCode.peek().getKey());
        return new ArrayList<>();
    }






}
