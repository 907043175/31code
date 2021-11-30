package com.code31.common.baseservice.utils;

import com.code31.common.baseservice.common.exception.SysException;
import com.google.common.base.Strings;
import com.code31.common.baseservice.common.CoreLoggers;
import com.code31.common.baseservice.common.SystemConst;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.HttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Utils {
    public static Properties readProperties(String name) {
        String filePath = Utils.class.getClassLoader().getResource(name).getPath();
        try (FileInputStream in = new FileInputStream(filePath)) {
            Properties p = new Properties();
            p.load(in);

            return p;
        } catch (Exception e) {
            throw new SysException(e);
        }
    }

    public static String createStr(String str, Object... params) {
        return String.format(str, params);
    }


    public static String trimString(String desc, Integer maxLen) {
        if (StringUtils.isEmpty(desc))
            return null;
        String desc1 = desc.trim();
        desc1 = desc1.replace("'", "");
        desc1 = desc1.replace("/", "");
        desc1 = desc1.replace("&", "");
        desc1 = desc1.replace("*", "");

        desc1 = desc1.replaceAll("\\\\", "\\\\\\\\");
        desc1 = desc1.replaceAll("\\n", "\\\\n");
        desc1 = desc1.replaceAll("\\r", "\\\\r");
        desc1 = desc1.replaceAll("\\00", "\\\\0");
        desc1 = desc1.replaceAll("'", "\\\\'");

        if (maxLen != null) {
            if (desc1.length() >= maxLen) {
                return desc1.substring(0, maxLen - 2);
            }
        }

        return desc1;
    }

    public static String encodePassword(String account,String password){
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password))
            return null;

        String saltPassword = SHAUtil.encode(account+password+ SystemConst.PASSPOD_SALT);
        return saltPassword;
    }


    public static boolean isValidIp(final String ip) {
        return (!Strings.isNullOrEmpty(ip)) && (!"unknown".equalsIgnoreCase(ip));
    }

    public static String getIpAddr(HttpRequest request) {

        try {

            javax.ws.rs.core.HttpHeaders headers = request.getHttpHeaders();

            String ip = headers.getHeaderString("X-Real-IP");

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString(" X-Forwarded-For");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("WL-Proxy-Client-IP");
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = headers.getHeaderString("WL-Proxy-Client-IP");
            }

            return ip;

        } catch (Exception e) {
            CoreLoggers.errorLogger.error("getIpAddr Exception：" , e);
        }

        return null;
    }


    public static String getFileName(String filePath) {
        try {

            int index = filePath.lastIndexOf(File.separator);
            if (index < 0)
                return filePath;
            if (index + 1 >= filePath.length())
                return "";

            String filePath2 = filePath.
                    substring(index + 1);

            return filePath2;

        } catch (Exception e) {
            CoreLoggers.errorLogger.error(e.getMessage(), e);
        }

        return null;
    }

}
