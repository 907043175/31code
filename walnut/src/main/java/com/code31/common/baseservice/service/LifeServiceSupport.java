package com.code31.common.baseservice.service;

import com.google.common.util.concurrent.AbstractService;


public class LifeServiceSupport extends AbstractService implements ILifeService {
//    @Override
//    public final State stopAndWait() {
//        return super.stopAndWait();
//    }
//
//    @Override
//    public final State startAndWait() {
//        return super.startAndWait();
//    }

    @Override
    protected final void doStart() {
        try {
            execStart();
            notifyStarted();
        } catch (Exception e) {
            notifyFailed(e);
        }
    }

    @Override
    protected final void doStop() {
        try {
            execStop();
            notifyStopped();
        } catch (Exception e) {
            notifyFailed(e);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * 实际的启动操作
     */
    protected void execStart() {

    }

    /**
     * 实际的停止操作
     */
    protected void execStop() {

    }
}
