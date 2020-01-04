package com.example.voip_app.util.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prefs {
    private static final String PREFS_NAME = "my_prefs";

    private static Prefs sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Gson mGson;

    public static synchronized Prefs getInstance() {
        if (sInstance == null) {
            sInstance = new Prefs();
        }
        return sInstance;
    }

    public void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mGson = new Gson();
    }

    public Prefs put(String key, Object value) {
        if (null != value) {
            if (value instanceof Integer) {
                mEditor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                mEditor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                mEditor.putFloat(key, (Float) value);
            } else if (value instanceof String) {
                mEditor.putString(key, (String) value);
            } else if (value instanceof Long) {
                mEditor.putLong(key, (Long) value);
            } else if (value instanceof Double) {
                putDouble(key, (Double) value);
            } else {
                String json = mGson.toJson(value);
                mEditor.putString(key, json);
            }
            mEditor.commit();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        if (type == Integer.class) {
            Integer value = mSharedPreferences.getInt(key, 0);
            return (T) value;
        } else if (type == Boolean.class) {
            Boolean value = mSharedPreferences.getBoolean(key, false);
            return (T) value;
        } else if (type == Float.class) {
            Float value = mSharedPreferences.getFloat(key, 0);
            return (T) value;
        } else if (type == String.class) {
            String value = mSharedPreferences.getString(key, "");
            return (T) value;
        } else if (type == Long.class) {
            Long value = mSharedPreferences.getLong(key, 0);
            return (T) value;
        } else if (type == Double.class) {
            Double value = getDouble(key, 0);
            return (T) value;
        } else {
            String json = mSharedPreferences.getString(key, "");
            if (TextUtils.isEmpty(json)) {
                return null;
            } else {
                return mGson.fromJson(json, type);
            }
        }
    }

    /**
     * Get value which is saved with a default value
     */
    public <T> T get(String key, Class<T> type, T defaultValue) {
        if (mSharedPreferences.contains(key)) {
            return get(key, type);
        } else {
            return defaultValue;
        }
    }

    /**
     * Get List of object which is saved
     */
    public <T> List<T> gets(String key, Class<T[]> clazz) {
        String json = mSharedPreferences.getString(key, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T[] values = mGson.fromJson(json, clazz);
        return new ArrayList<T>(Arrays.asList(values));
    }

    /**
     * remove a key/value pair
     */
    public void remove(String key) {
        mEditor.remove(key).commit();
    }

    /**
     * Clear all
     */
    public void clear() {
        mEditor.clear().commit();
    }

    private void putDouble(final String key, final double value) {
        mEditor.putLong(key, Double.doubleToRawLongBits(value));
    }

    private double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(mSharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
