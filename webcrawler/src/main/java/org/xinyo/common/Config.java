package org.xinyo.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private final static Properties CONFIG_PROPERTIES = new Properties();


    public static void initConfig(String configFilePath) {
        try (FileInputStream fileInputStream = new FileInputStream(configFilePath)) {
            CONFIG_PROPERTIES.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        try {
            return CONFIG_PROPERTIES.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getIntValue(String key) {
        String value = CONFIG_PROPERTIES.getProperty(key);
        return Integer.parseInt(value);
    }
}
