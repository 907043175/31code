package com.code31.common.baseservice.common;


public interface INameResourceService<T> {

    T get(String name) throws Exception;

    public void invalidate(String name);
}
