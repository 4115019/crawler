package ca.credits.base.event;

import ca.credits.base.AbstractExecutive;
import ca.credits.base.BeansFactory;
import ca.credits.base.IExecutive;
import ca.credits.base.IExecutiveManager;
import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.engine.IEngine;
import ca.credits.base.gateway.IGateway;
import ca.credits.base.queue.IDuplicateKey;

import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
public abstract class AbstractEvent extends AbstractExecutive implements IEvent,IDuplicateKey {
    protected String workerClassName;

    protected AbstractEvent(String workerClassName, String activityId,AbstractNode node,IExecutiveManager regulator){
        super(activityId,node,regulator);
        this.workerClassName = workerClassName;
    }

    @Override
    public void run() {
        BeansFactory.getContext().getBean(IEngine.class).execute(this);
    }

    @Override
    public String getWorkerClassName() {
        return workerClassName;
    }

    @Override
    public String getDuplicateKey() {
        return null;
    }
}
