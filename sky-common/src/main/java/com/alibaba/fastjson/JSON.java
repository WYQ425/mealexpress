package com.alibaba.fastjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public final class JSON {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JSON() {
    }

    public static String toJSONString(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize object to JSON", e);
        }
    }

    public static JSONObject parseObject(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = OBJECT_MAPPER.readValue(json, Map.class);
            return new JSONObject(map);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to parse JSON string", e);
        }
    }
}
