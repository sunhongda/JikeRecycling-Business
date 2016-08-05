
package com.buslink.busjie.driver.util;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/9.
 */
public class XString {
    public static void remove(JSONObject jsonObject, String name) {
        if (jsonObject.has(name)) {
            jsonObject.remove(name);
        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String name) {
        JSONObject res = null;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getJSONObject(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        JSONObject res = null;
        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
        JSONArray res = null;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getJSONArray(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static int getInt(JSONObject jsonObject, String name) {
        int res = 0;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getInt(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static long getLong(JSONObject jsonObject, String name) {
        long res = 0;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getLong(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getStr(JSONObject jsonObject, String name) {
        String res = "";
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getString(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static double getDouble(JSONObject jsonObject, String name) {
        double res = 0.00;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getDouble(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean getBoolean(JSONObject jsonObject, String name) {
        boolean res = false;
        try {
            if (jsonObject.has(name)) {
                res = TextUtils.isEmpty(jsonObject.getString(name)) ? res : jsonObject.getBoolean(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static JSONObject put(JSONObject jsonObject, String name, Object object) {
        try {
            jsonObject.put(name, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static List<JSONObject> getList(JSONObject jsonObject, String name) {
        JSONArray a = getJSONArray(jsonObject, name);
        List<JSONObject> list = new LinkedList<>();
        int size = a == null ? 0 : a.length();
        for (int i = 0; i < size; i++) {
            try {
                list.add(a.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    public static JSONObject getJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
