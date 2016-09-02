package ca.credits.base;

import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.diagram.AbstractTaskNode;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.IGateway;
import ca.credits.base.task.ITask;
import ca.credits.common.ListUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenwen on 16/8/26.
 */
public abstract class AbstractExecutive implements IExecutive {
    /**
     * activityId
     */
    protected String activityId;

    /**
     * id
     */
    protected String id;

    /**
     * task id
     */
    protected String taskId;

    /**
     * all listener
     */
    protected Vector<IListener> listeners;

    /**
     * event status, default not start
     */
    protected AtomicInteger status;

    /**
     * the execute node
     */
    @Getter
    protected AbstractNode node;

    /**
     * regulator manager
     */
    protected IExecutiveManager regulator;

    /**
     * exception
     */
    protected Throwable throwable;

    /**
     * the default constructor
     */
    public AbstractExecutive(String activityId, AbstractNode node, IExecutiveManager regulator){
        status = new AtomicInteger(Status.UNDO.ordinal());
        listeners = new Vector<>();
        this.node = node;
        this.activityId = activityId;
        this.regulator = regulator;
    }

    /**
     * when event start run
     * @param subject run event
     * @param args event args
     */
    @Override
    public void onStart(final ISubject subject, final Object args) {
        /**
         * start event by this, then change event status to running and notify all listener
         */
        status.set(Status.RUNNING.ordinal());
        if (ListUtil.isNotEmpty(listeners)) {
            listeners.parallelStream().forEach(listener -> listener.onStart(subject, args));
        }
    }

    /**
     * when subject complete
     * @param subject complete subject
     * @param args args
     */
    @Override
    public void onComplete(ISubject subject, Object args) {
        /**
         * complete event by this, then change event status to done and notify all listener
         */
        status.set(Status.DONE.ordinal());
        if (ListUtil.isNotEmpty(listeners)) {
            listeners.parallelStream().forEach(listener -> listener.onComplete(subject, args));
        }
        /**
         * notify this regulator
         */
        regulator.complete(this, args);
    }

    /**
     * then event run throw exception
     * @param subject exception event
     * @param throwable exception
     * @param args args
     */
    @Override
    public void onThrowable(ISubject subject, Throwable throwable, Object args) {
        this.throwable = throwable;
        status.set(Status.EXCEPTION.ordinal());
        if (ListUtil.isNotEmpty(listeners)) {
            listeners.parallelStream().forEach(listener -> listener.onThrowable(subject, throwable, args));
        }
        /**
         * notify this regulator
         */
        regulator.exception(this, throwable, args);
    }

    /**
     * register listener
     * @param listener listener
     */
    @Override
    public void registerListener(IListener listener){
        if (listener == null)
            throw new NullPointerException();

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * remove listener
     * @param listener listener
     */
    @Override
    public void removeListener(IListener listener){
        listeners.removeElement(listener);
    }

    /**
     * remove all listener
     */
    @Override
    public void removeAll(){
        listeners.removeAllElements();
    }

    @Override
    public String getActivityId() {
        return activityId;
    }

    @Override
    public String getTaskId() {
        return taskId = (taskId != null ? taskId : this instanceof ITask ? node.getId() : this instanceof IEvent ? ((ITask)regulator).getTaskId() : null);
    }

    @Override
    public String getId() {
        return id = (id != null ? id : node.getId());
    }

    @Override
    public Status getStatus() {
        return Status.getValue(status.get());
    }
}
