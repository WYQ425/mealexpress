package com.alibaba.fastjson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSONArray extends ArrayList<Object> {

    public JSONArray() {
        super();
    }

    JSONArray(List<Object> values) {
        super();
        if (values != null) {
            values.forEach(this::addWrapped);
        }
    }

    @Override
    public boolean add(Object value) {
        return super.add(wrap(value));
    }

    @Override
    public void add(int index, Object element) {
        super.add(index, wrap(element));
    }

    @Override
    public Object set(int index, Object element) {
        return super.set(index, wrap(element));
    }

    public JSONObject getJSONObject(int index) {
        Object value = get(index);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        return null;
    }

    private void addWrapped(Object value) {
        super.add(wrap(value));
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
