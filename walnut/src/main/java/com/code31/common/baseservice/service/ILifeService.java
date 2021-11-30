package com.code31.common.baseservice.service;

import com.google.common.util.concurrent.Service;


public interface ILifeService extends Service {
    /**
     * 取得服务的名称
     *
     * @return
     */
    String getName();
}
