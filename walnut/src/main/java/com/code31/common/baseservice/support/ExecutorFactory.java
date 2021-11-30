package com.code31.common.baseservice.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.*;

public class ExecutorFactory {
    public static volatile ExecutorService exService = null;
    private static volatile ExecutorService _exService2 = null;

    static Map<String, Object> _lockMap;

    static {
        _lockMap = Maps.newConcurrentMap();
    }

    public static Object getLockObj(String key)  {
        Preconditions.checkArgument(key != null);

        Object obj =  _lockMap.get(key);
        if (obj != null){
            return obj;
        }

        synchronized(ExecutorFactory.class){
            obj =  _lockMap.get(key);
            if (obj == null){
                obj = new Object();
                _lockMap.put(key,obj);
            }
            return obj;
        }
    }


    /**
     * 获取线程池
     *
     * @return
     */
    public static Executor getExecutor() {
        if (exService == null) {
            //需要使用同步防止多个线程实例化
            synchronized (ExecutorFactory.class) {
                if (exService == null) {
                    Runtime runtime = Runtime.getRuntime();
                    int nrOfProcessors = runtime.availableProcessors()*2;
                    exService = Executors.newFixedThreadPool(nrOfProcessors*2);
                }
            }
        }
        return exService;
    }


    public static ExecutorService getExecutorService() {
        if (_exService2 == null) {
            //需要使用同步防止多个线程实例化
            synchronized (ExecutorFactory.class) {
                if (_exService2 == null) {
                    Runtime runtime = Runtime.getRuntime();
                    int nrOfProcessors = runtime.availableProcessors()*2;
                    //     logger.info("ExecutorService number:" + nrOfProcessors);
                    _exService2 = new ThreadPoolExecutor(nrOfProcessors, nrOfProcessors*2,
                            10L, TimeUnit.MINUTES,
                            new LinkedBlockingQueue<Runnable>());
                }
            }
        }
        return _exService2;
    }

}
