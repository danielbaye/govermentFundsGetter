package com.training.myfapplication;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class infoGetter {




    infoGetter() {
    }


    public class HttpRequestTask extends Thread {
        private String url;
        private String response="0";


        HttpRequestTask(String url){
            this.url = url;
            this.response="";
        }

        @Override
        public void run() {
            super.run();

            this.response="1";
            try {
                URL apiUrl = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                // Set request method and headers
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                this.response = "2";
                // Get the response code
                int responseCode = connection.getResponseCode();
                // Read the response body
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder response = new StringBuilder();
                this.response = response.toString();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Disconnect the connection
                connection.disconnect();

                // Return the response as a string
                this.response = response.toString();

            } catch (IOException e) {
                e.printStackTrace();
                this.response = "fail" + e.toString();
            }



        }

        public String getResponse() {
            return response;
        }
    }

    public String getQueryResponse(String query){
        HttpRequestTask task = new HttpRequestTask(query);

        task.start();
        while (task.isAlive()){
        }
        return task.getResponse();

    }

    public ArrayList<String> getTitles(String s){
        ArrayList<String> titleList = new ArrayList<>();

        try {
            JSONArray array = new JSONArray("["+s+"]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")){
                    titleList.add(obj.getString("title"));

            }

        }} catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

        return titleList;
    }

    public Map<String,Float> getSumByYear(String s,boolean a){
        Map<String,Float> sumByYearMap = new HashMap<>();

        try {
            JSONArray array = new JSONArray("["+s+"]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            int i;
            for ( i = 0; i <= (a?(array.length()-1):0) ; i++) {
                JSONObject obj = array.getJSONObject(i);
//                JSONObject obj = array.getJSONObject(0);
                if (obj.has("history")){
                    JSONObject history = obj.getJSONObject("history");

                    for (int j = 0; j < history.length(); j++) {
                        String year = String.valueOf(history.names().get(j));
                        if(!sumByYearMap.containsKey(year))
                            sumByYearMap.put(year,0.0f);
                        JSONObject actualObject = (JSONObject) history.get(year);
                            sumByYearMap.put(year,sumByYearMap.get(year)+ maxOfThree(actualObject));
                    }}
                if (obj.get("year").toString().equals("2024"))
                    sumByYearMap.put("2024",maxOfThree(obj));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    return sumByYearMap;
    }

    public Map<String,String> getDepartments(String s) {

        Map<String,String> departmentsMap = new HashMap<>();

        try {
            JSONArray array = new JSONArray("["+s+"]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")){
                    departmentsMap.put(obj.getString("title"),obj.getString("code"));

                }

            }} catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

        return departmentsMap;

    }

    public Map<String,String> getDepartmentsFromChildren(String s) {

        Map<String,String> departmentsMap = new HashMap<>();

        try {
            JSONArray array = new JSONArray("["+s+"]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            if(((JSONObject) array.get(0)).has("children"))
                if(((JSONObject) array.get(0)).getString("children").equals("null"))
                return departmentsMap;
            array = (JSONArray) ((JSONObject) array.get(0)).get("children");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")){
                    departmentsMap.put(obj.getString("title"),obj.getString("code"));
                }

            }} catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return departmentsMap;

    }

    public Map<String,Float> getJustBudgetByYear(String s){
        Map<String,Float> sumByYearMap = new HashMap<>();
        try {
            JSONArray array = new JSONArray("["+s+"]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            JSONObject obj = array.getJSONObject(0);
            if (obj.has("history")){
                JSONObject history = obj.getJSONObject("history");

                for (int j = 0; j < history.length(); j++) {
                    String year = String.valueOf(history.names().get(j));
                    if(!sumByYearMap.containsKey(year))
                        sumByYearMap.put(year,0.0f);
                    JSONObject actualObject = (JSONObject) history.get(year);

                    if (!actualObject.isNull("net_executed"))

                        sumByYearMap.put(year,sumByYearMap.get(year)+ maxOfThree(actualObject));
                }}
            if (obj.get("year").toString().equals("2024"))
                sumByYearMap.put("2024",maxOfThree(obj));

        } catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);

        }
        return sumByYearMap;
    }

    public Float maxOfThree(JSONObject actualObject) throws JSONException {
        Double max = null;
        Double a =  !actualObject.isNull("net_executed")?
                actualObject.getDouble("net_executed"):0.0f;
        Double b =  !actualObject.isNull("net_allocated")?
                actualObject.getDouble("net_allocated"):0.0f;
        Double c =  !actualObject.isNull("net_revised")?
                actualObject.getDouble("net_revised"):0.0f;
        if (a != null && (max == null || a > max)) {
            max = a;
        }
        if (b != null && (max == null || b > max)) {
            max = b;
        }
        if (c != null && (max == null || c > max)) {
            max = c;
        }

        return  max.floatValue();
    }

}






