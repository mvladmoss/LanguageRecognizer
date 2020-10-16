package com.bsuir.iyazis.languagerecognizer.utils;

import com.bsuir.iyazis.languagerecognizer.service.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JsonHelper {
    private final ObjectMapper mapper;

    @Autowired
    public JsonHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new ServiceException("Unable to serialize an object to json", e);
        }
    }

    public <T> T parse(String value, Class<T> clazz) {
        try {
            return mapper.readValue(value, clazz);
        } catch (IOException e) {
            throw new ServiceException("Unable to parse " + clazz.getSimpleName() + " from json", e);
        }
    }

    public <T> List<T> parseList(String value, Class<T> clazz) {
        try {
            return mapper.readValue(value, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new ServiceException("Unable to parse a list of " + clazz.getSimpleName(), e);
        }
    }

    public Map<String, String> parseMap(String value) {
        return parseMap(value, String.class);
    }

    public <T> Map<String, T> parseMap(String value, Class<T> valueClass) {
        try {
            return mapper.readValue(value, mapper.getTypeFactory()
                    .constructMapType(Map.class, String.class, valueClass));
        } catch (IOException e) {
            throw new ServiceException("Unable to parse a map with " + valueClass.getSimpleName() + " values", e);
        }
    }
}
