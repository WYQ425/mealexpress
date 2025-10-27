package com.alibaba.fastjson;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONObject extends LinkedHashMap<String, Object> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JSONObject() {
        super();
    }

    JSONObject(Map<String, Object> values) {
        super();
        if (values != null) {
            values.forEach((key, value) -> super.put(key, wrap(value)));
        }
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key, wrap(value));
    }

    public JSONObject getJSONObject(String key) {
        Object value = get(key);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            JSONObject object = new JSONObject(map);
            super.put(key, object);
            return object;
        }
        return null;
    }

    public JSONArray getJSONArray(String key) {
        Object value = get(key);
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            JSONArray array = new JSONArray(list);
            super.put(key, array);
            return array;
        }
        return null;
    }

    public String getString(String key) {
        Object value = get(key);
        return value != null ? String.valueOf(value) : null;
    }

    public <T> T toJavaObject(Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(this, clazz);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    private Object wrap(Object value) {
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return new JSONObject(map);
        }
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            return new JSONArray(list);
        }
        return value;
    }
}
