package ca.credits.base.task;

import ca.credits.base.IExecutive;
import ca.credits.base.IExecutiveManager;
import ca.credits.base.event.IEvent;

/**
 * Created by chenwen on 16/8/26.
 */
public interface ITask extends IExecutive,IExecutiveManager {
    /**
     * get start event
     * @return start event
     */
    IEvent getStartEvent();

    /**
     * get end event
     * @return end event
     */
    IEvent getEndEvent();

    /**
     * set start event
     * @param startEvent start event
     */
    void setStartEvent(IEvent startEvent);

    /**
     * set end event
     * @param endEvent end event
     */
    void setEndEvent(IEvent endEvent);
}

