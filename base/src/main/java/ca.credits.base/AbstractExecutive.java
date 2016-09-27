package ca.credits.base;

import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.event.IEvent;
import ca.credits.base.task.ITask;
import ca.credits.base.util.StringUtils;
import ca.credits.common.util.ListUtil;
import ca.credits.common.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chenwen on 16/8/26.
 */
@Slf4j
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
     * child id
     */
    protected AtomicLong childId;

    /**
     * the default constructor
     */
    public AbstractExecutive(String activityId, AbstractNode node, IExecutiveManager regulator){
        status = new AtomicInteger(Status.UNDO.ordinal());
        listeners = new Vector<>();
        this.node = node;
        this.activityId = activityId;
        this.regulator = regulator;
        this.childId = new AtomicLong(0);
    }

    /**
     * when event start run
     * @param subject run event
     * @param args event args
     */
    @Override
    public void onStart(final ISubject subject, final Properties args) {
        if (log.isDebugEnabled()){
            log.debug(StringUtils.formatExecutive((IExecutive) subject,args,"start run"));
        }
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
    public void onComplete(ISubject subject, Properties args) {
        if (log.isDebugEnabled()){
            log.debug(StringUtils.formatExecutive((IExecutive) subject,args,"success complete"));
        }
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
    public void onThrowable(ISubject subject, Throwable throwable, Properties args) {
        log.error(StringUtils.formatExecutive((IExecutive) subject,args,"exec throw exception"),throwable);
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

    @Override
    public boolean isComplete() {
        return getStatus() == Status.EXCEPTION || getStatus() == Status.DONE;
    }

    @Override
    public String getRandomChildId() {
        return String.format("%s.%s.%s",getActivityId(),getId(),childId.incrementAndGet());
    }
}
