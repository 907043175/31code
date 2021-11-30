package com.code31.common.baseservice.support.filter;

public interface ShiroFilterHandle {

    boolean checkUserPermission(String authToken, String[] permissions);
}
