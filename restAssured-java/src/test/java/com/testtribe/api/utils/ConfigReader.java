package com.testtribe.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("config.properties not found on the classpath");
            }
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Failed to load config.properties: " + e.getMessage());
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required config key: " + key);
        }
        return value;
    }
}
