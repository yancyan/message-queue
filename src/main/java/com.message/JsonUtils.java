package com.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    public static Logger log = LoggerFactory.getLogger(JsonUtils.class);


    public static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();


        objectMapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String encode(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and class is [{}]",content,valueType.getName());
            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(content, typeReference);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and type reference is [{}]",content,typeReference.getType().getTypeName());

            throw new RuntimeException(e);
        }
    }

    public static <T> T decode(String content, JavaType javaType) {
        try {
            return objectMapper.readValue(content, javaType);
        } catch (Exception e) {
            log.error("JsonUtils.decode.error.content is [{}] and type is [{}]",content,javaType.getRawClass().getName());
            throw new RuntimeException(e);
        }
    }
}
