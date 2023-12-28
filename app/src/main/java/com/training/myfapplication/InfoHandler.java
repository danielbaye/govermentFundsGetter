package com.training.myfapplication;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class InfoHandler {

    private Context context;
    private Stack<Map.Entry<String, String>> currentDepartmentCode;
    private String url;

    public InfoHandler(String url, Context context) {
        this.url = url;
        this.context = context;
        currentDepartmentCode = new Stack<>();
        currentDepartmentCode.push(new AbstractMap.SimpleEntry<>("00", "הכל"));
    }

    public Map<String, Map<String, Float>> getDepartmentsAndValues(
            ArrayList<String> codes) {
        infoGetter ig = new infoGetter();
        String t = "";
        for (int i = 0; i < codes.size(); i++) {
            t += " or code like '" + codes.get(i) + "'";
        }
        String curYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1);

        String s = ig.getQueryResponse(url + "SELECT * FROM budget " +
                "where year = " + curYear.toString() + " and code like '' " + t);
        String title = "";

        Map<String, Map<String, Float>> EntryMap = new HashMap<>();
        JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray("[" + s + "]").getJSONObject(0)
                    .getJSONArray("rows");
            // .getJSONObject(0).getJSONArray("children");
            Map<String, Float> history;
            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                if (obj.has("year")) {
                    if (obj.getJSONObject("history").length() > 0) {
                        history = getHistory(obj.getJSONObject("history"));
                        if (obj.get("year").toString().equals(curYear)) {
                            history.put(curYear, maxOfThree(obj));
                        }
                        title = obj.getString("title");
                        if (!EntryMap.containsKey(title))
                            EntryMap.put(title, history);
                        else if (EntryMap.get(title).size() < history.size())
                            EntryMap.put(title, history);

                    }
                }
            }

        } catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }
        return EntryMap;
    }

    public ArrayList<String> getTitles(String newText) {

        infoGetter ig = new infoGetter();
        String s = ig.getQueryResponse(
                url + "SELECT title FROM budget where title" +
                        " like '%%" + newText + "%%' and (EXISTS (SELECT 1 " +
                        "FROM jsonb_array_elements(hierarchy) AS inner_array " +
                        "WHERE inner_array->>0 = '" + currentDepartmentCode.peek().getKey() + "') " +
                        "or code = '" + currentDepartmentCode.peek().getKey() + "')");

        ArrayList<String> titleList = new ArrayList<>();

        try {
            JSONArray array = new JSONArray("[" + s + "]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")) {
                    titleList.add(obj.getString("title"));

                }

            }
        } catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

        catch (RuntimeException e) {
            Toast.makeText(context, "failed getTitles", Toast.LENGTH_SHORT).show();
        }
        return titleList;
    }

    public Map<String, Float> getSumByYear(String currentQuery) {
        Map<String, Float> sumByYearMap = new HashMap<>();
        infoGetter inf = new infoGetter();
        boolean a = (currentQuery.length() == 0);
        String s = inf.getQueryResponse(url + "SELECT * FROM budget where title" +
                " like '%%" + currentQuery + "%%' " +
                (a ? "and code = '" + currentDepartmentCode.peek().getKey() + "'"
                        : ("and (EXISTS (SELECT 1 " +
                                "FROM jsonb_array_elements(hierarchy) AS inner_array " +
                                "WHERE inner_array->>0 = '" + currentDepartmentCode.peek().getKey() + "') or"
                                + " code = '" + currentDepartmentCode.peek().getKey() + "')")));
        String curYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1);

        try {
            JSONArray array = new JSONArray("[" + s + "]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            int i;
            for (i = 0; i <= (a ? (array.length() - 1) : 0); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (obj.has("history")) {
                    JSONObject history = obj.getJSONObject("history");

                    for (int j = 0; j < history.length(); j++) {
                        String year = String.valueOf(history.names().get(j));
                        if (!sumByYearMap.containsKey(year))
                            sumByYearMap.put(year, 0.0f);
                        JSONObject actualObject = (JSONObject) history.get(year);
                        sumByYearMap.put(year, sumByYearMap.get(year) + maxOfThree(actualObject));
                    }
                }
                if (obj.get("year").toString().equals(curYear))
                    sumByYearMap.put(curYear, maxOfThree(obj));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            Toast.makeText(context, "failed getSumByYear", Toast.LENGTH_SHORT).show();
        }

        return sumByYearMap;
    }

    public Map<String, String> getDepartments() {

        infoGetter departmentsGetter = new infoGetter();
        String urlString = "";
        urlString = url + "SELECT * FROM budget " +
                "where parent isnull";
        String s = departmentsGetter.getQueryResponse(urlString);

        Map<String, String> departmentsMap = new HashMap<>();
        try {
            JSONArray array = new JSONArray("[" + s + "]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")) {
                    departmentsMap.put(obj.getString("title"), obj.getString("code"));

                }

            }
        } catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            Toast.makeText(context, "failed getDepartments", Toast.LENGTH_SHORT).show();
        }

        return departmentsMap;

    }

    public Map<String, String> getDepartmentsFromChildren(String newDepartmentCode) {

        infoGetter departmentsGetter = new infoGetter();

        String s = departmentsGetter.getQueryResponse(url + "SELECT children FROM budget " +
                "where code " + departmentCode_translator(newDepartmentCode));
        Map<String, String> departmentsMap = new HashMap<>();

        try {

            JSONArray array = new JSONArray("[" + s + "]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            if (((JSONObject) array.get(0)).has("children"))
                if (((JSONObject) array.get(0)).getString("children").equals("null"))
                    return departmentsMap;
            array = (JSONArray) ((JSONObject) array.get(0)).get("children");

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.isNull("title")) {
                    departmentsMap.put(obj.getString("title"), obj.getString("code"));
                }

            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            Toast.makeText(context, "failed getDepartmentsFromChildren", Toast.LENGTH_SHORT).show();
        }

        return departmentsMap;

    }

    public Map<String, Float> getJustBudgetByYear() {
        infoGetter inf = new infoGetter();
        String s = inf.getQueryResponse(url + "SELECT * FROM budget where code like '00'");
        Map<String, Float> sumByYearMap = new HashMap<>();
        String curYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1);

        try {
            JSONArray array = new JSONArray("[" + s + "]");
            array = (JSONArray) array.getJSONObject(0).get("rows");
            JSONObject obj = array.getJSONObject(0);
            if (obj.has("history")) {
                JSONObject history = obj.getJSONObject("history");

                for (int j = 0; j < history.length(); j++) {
                    String year = String.valueOf(history.names().get(j));
                    if (!sumByYearMap.containsKey(year))
                        sumByYearMap.put(year, 0.0f);
                    JSONObject actualObject = (JSONObject) history.get(year);
                    sumByYearMap.put(year, sumByYearMap.get(year) + maxOfThree(actualObject));
                }
            }
            if (obj.get("year").toString().equals(curYear))
                sumByYearMap.put(curYear, maxOfThree(obj));

        } catch (JSONException e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);

        } catch (RuntimeException e) {
            Toast.makeText(context, "failed getJustBudgetByYear", Toast.LENGTH_SHORT).show();
        }
        return sumByYearMap;

    }

    public Float maxOfThree(JSONObject actualObject) throws JSONException {
        Double max = null;
        Double a = !actualObject.isNull("net_executed") ? actualObject.getDouble("net_executed") : 0.0f;
        Double b = !actualObject.isNull("net_allocated") ? actualObject.getDouble("net_allocated") : 0.0f;
        Double c = !actualObject.isNull("net_revised") ? actualObject.getDouble("net_revised") : 0.0f;
        if (a != null && (max == null || a > max)) {
            max = a;
        }
        if (b != null && (max == null || b > max)) {
            max = b;
        }
        if (c != null && (max == null || c > max)) {
            max = c;
        }

        return max.floatValue();
    }

    public Float maxOfTwo(JSONObject actualObject) throws JSONException {
        Double max = 0.0;
        Double a = !actualObject.isNull("net_revised") ? actualObject.getDouble("net_revised") : 0.0f;
        Double b = !actualObject.isNull("net_allocated") ? actualObject.getDouble("net_allocated") : 0.0f;

        if (a != null && (max == null || a > max)) {
            max = a;
        }
        if (b != null && (max == null || b > max)) {
            max = b;
        }

        return max.floatValue();
    }

    private String departmentCode_translator(String s) {
        if (s.equals(""))
            return "isnull";
        else
            return "like '" + s + "'";
    }

    public void addDepartment(Map.Entry<String, String> E) {
        currentDepartmentCode.push(E);
    }

    public Map.Entry<String, String> popDepartment() {
        return currentDepartmentCode.pop();
    }

    public Map.Entry<String, String> peekDepartment() {
        return currentDepartmentCode.peek();
    }

    public void clearDepartment() {
        currentDepartmentCode.clear();
    }

    public int sizeDepartment() {
        return currentDepartmentCode.size();
    }

    public String[] getDepartmentNames() {
        String names[] = new String[currentDepartmentCode.size()];
        for (int i = 0; i < currentDepartmentCode.size(); i++) {
            names[i] = currentDepartmentCode.get(i).getValue();

        }
        return names;
    }

    private Map<String, Float> getHistory(JSONObject history) throws JSONException {
        Map<String, Float> sumByYearMap = new HashMap<>();

        for (int j = 0; j < history.length(); j++) {
            String year = String.valueOf(history.names().get(j));
            if (!sumByYearMap.containsKey(year))
                sumByYearMap.put(year, 0.0f);
            JSONObject actualObject = (JSONObject) history.get(year);

            sumByYearMap.put(year, maxOfThree(actualObject));
        }

        return sumByYearMap;
    }

}
