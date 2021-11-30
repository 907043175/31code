package com.code31.common.baseservice.app.internal;


import com.code31.common.baseservice.service.annotation.ShutdownStage;


public class ShutdownStageRegistry extends StageActionRegistrySupport {
    @Override
    public String toString() {
        return ShutdownStage.class.toString();
    }
}
