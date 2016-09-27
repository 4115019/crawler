package ca.credits.base.engine;

import ca.credits.base.event.IEvent;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IEngine {
    /**
     * execute event
     * @param event run event
     */
    void execute(IEvent event);

    /**
     * register workerManager's worker
     * @param workerManager
     * @param workerClass
     */
    <T extends AbstractWorker> void register(IWorkerManager workerManager,Class<T> workerClass);
}
