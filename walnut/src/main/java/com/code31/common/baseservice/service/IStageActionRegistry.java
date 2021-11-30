package com.code31.common.baseservice.service;


public interface IStageActionRegistry {
    /**
     * @param action
     */
    public void addAction(Runnable action);

    /**
     * 执行所有的操作
     */
    public void execute();
}
