package com.code31.common.baseservice.common.log;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Map;

public class LogUtils {

    private static Map<String, String> _logResonsMap = Maps.newConcurrentMap();

    public static  String buildLogObj(LogCommonReasons.ILogReason logReason, Object ... params) {
        String logKey = logReason.toString();

        if(_logResonsMap.containsKey(logKey)){
            String reson = _logResonsMap.get(logKey);

            return  MessageFormat.format(logReason.getReasonText(), params);
        }


        Field[] fields= logReason.getClass().getDeclaredFields();

        if(fields != null){
            for(Field f : fields){
                LogCommonReasons.ReasonDesc meta = f.getAnnotation(LogCommonReasons.ReasonDesc.class);
                if(meta!=null){
                    _logResonsMap.put(f.getName(), meta.value());
                }
            }
        }

   //     String reson = _logResonsMap.get(logKey);
        return  MessageFormat.format(logReason.getReasonText(), params);

    }
}
