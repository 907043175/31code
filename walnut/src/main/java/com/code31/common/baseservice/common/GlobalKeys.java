package com.code31.common.baseservice.common;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.util.Map;

public final class GlobalKeys {

    public static final String APP_GLOBAL_ARG_KEY_NAME = "APP.GLOBAL.ARG";

    public static final String APP_GLOBAL_CONF_KEY_NAME = "APP.GLOBAL.CONF";

    public static final String VALIDATION_RULE_CONF = "VALIDATION.RULE.CONF";

    public static final String VALIDATION_STRICT_MODE = "VALIDATION.STRICT.MODE";

    public static final String PARAM_BEAN_CLASSES = "PARAM.BEAN.CLASSES";

    public static final String PARAM_BEAN_IMPL_MAP = "PARAM.BEAN.IMPL.MAP";

    public static final Key<ImmutableMap<String, String>> APP_GLOBAL_ARG_KEY = GuiceKeys.getKey(ImmutableMap.class, Names.named(APP_GLOBAL_ARG_KEY_NAME), String.class, String.class);

    public static final Key<ImmutableMap<String, String>> APP_GLOBAL_CONF_KEY = GuiceKeys.getKey(ImmutableMap.class, Names.named(APP_GLOBAL_CONF_KEY_NAME), String.class, String.class);

    public static final Key<Map<Class, Class>> PARAM_BEAN_IMPL_MAP_KEY = GuiceKeys.getKey(Map.class, Names.named(PARAM_BEAN_IMPL_MAP), Class.class, Class.class);

    private GlobalKeys() {

    }
}