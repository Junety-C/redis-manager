package org.junety.redis.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by caijt on 2017/3/6.
 */
public class PropertiesLoader {

    private static Map<String, Properties> propertiesMap = new HashMap<>();

    public static synchronized Properties load(String filename) {
        if (filename == null || filename.length() == 0) {
            throw new IllegalArgumentException("Properties filename can't not be null");
        }

        if (propertiesMap.containsKey(filename)) {
            return propertiesMap.get(filename);
        }

        Properties prop = new Properties();
        try {
            prop.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can not load properties from %s", filename));
        }

        propertiesMap.put(filename, prop);
        return prop;
    }

    private PropertiesLoader() {}
}