package com.code31.common.baseservice.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CommonNameResourceServiceImpl<T> implements INameResourceService<T> {
    private final LoadingCache<String, T> clientCache;

    public CommonNameResourceServiceImpl(CacheLoader<String, T> cacheLoader) {
        this.clientCache = CacheBuilder.newBuilder().build(cacheLoader);
    }

    @Override
    public T get(String name) throws Exception {
        return clientCache.get(name);
    }

    @Override
    public void invalidate(String name) {
        this.clientCache.invalidate(name);
    }

}
