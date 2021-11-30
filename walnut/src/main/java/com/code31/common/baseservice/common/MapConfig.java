package com.code31.common.baseservice.common;


import com.code31.common.baseservice.common.xml.XmlProperties;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;


public final class MapConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapConfig.class);

    private MapConfig() {

    }

    public static int getInt( String name, Map<String, String> config, int defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }


    public static byte getByte(  String name,  Map<String, String> config, byte defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Byte.parseByte(s);
    }


    public static long getLong(  String name,  Map<String, String> config, long defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Long.parseLong(s);
    }

    public static String getString(  String name,   Map<String, String> config, String defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return s;
    }

    public static boolean getBoolean(  String name,  Map<String, String> config, boolean defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Boolean.valueOf(s);
    }

    public static ImmutableMap<String, String> pasreConf(String appConfPath) {
        Map<String, String> all = Maps.newHashMap();
        if (appConfPath != null) {
            String[] appConfs = appConfPath.split(",");
            for (final String conf : appConfs) {
                LOGGER.info("Load config from " + conf);
                Map<String, String> confMap = XmlProperties.loadFromXml(conf);
                if (confMap != null) {
                    Set<Map.Entry<String, String>> entries = confMap.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        String key = entry.getKey();
                        String s = all.get(key);
                        if (s != null) {
                            LOGGER.warn("Found duplicate key {}:{},will be overrided by new value{},config:{}", new Object[]{key, s, entry.getValue(), conf});
                        }
                        all.put(key, entry.getValue());
                    }
                }
            }
        }
        return ImmutableMap.<String, String>builder().putAll(all).build();
    }
}
