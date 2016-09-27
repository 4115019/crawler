package ca.credits.base.engine;

import ca.credits.base.balance.IBalance;
import ca.credits.base.event.IEvent;

/**
 * Created by chenwen on 16/8/30.
 */
public interface IWorkerManager<T> extends IBalance{
    /**
     * poll event
     * @return event
     */
    T take() throws InterruptedException;

    /**
     * add event
     * @param event event
     */
    boolean add(IEvent event);

    /**
     * when worker is shutdown
     * @param worker
     */
    void done(AbstractWorker worker);
}
