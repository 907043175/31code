package com.code31.common.baseservice.app.internal;

import com.code31.common.baseservice.service.IStageActionRegistry;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class StageActionRegistrySupport implements IStageActionRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger("StageAction");
    private final List<Runnable> actions = Lists.newLinkedList();
    private boolean completed = false;

    @Override
    public synchronized void addAction(Runnable action) {
        Preconditions.checkState(!completed);
        Preconditions.checkArgument(action != null);
        LOGGER.info(this + " add action " + action);
        actions.add(action);
    }

    @Override
    public synchronized void execute() {
        if (completed) {
            LOGGER.warn("Actions has alerady completed,ignored. [" + this + "]");
            return;
        }
        completed = true;
        LOGGER.info("Run actions in " + this);
        for (Runnable action : actions) {
            try {
                LOGGER.info("Run action:" + action.getClass());
                action.run();
                LOGGER.info("Run action:" + action.getClass() + " finish");
            } catch (Exception e) {
                LOGGER.error("Execute action [" + action + "] fail.", e);
                throw new IllegalStateException("Exec stage action error.", e);
            }
        }
        LOGGER.info("Run actions in " + this + ",finish");
    }
}
