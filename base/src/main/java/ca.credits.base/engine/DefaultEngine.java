package ca.credits.base.engine;

import ca.credits.base.event.IEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenwen on 16/8/30.
 */
@Named("defaultEngine")
@Singleton
@Slf4j
public class DefaultEngine implements IEngine{
    /**
     * worker's class is key,IWorkerManager is value
     */
    protected Map<String,IWorkerManager> workerManagerMap = new ConcurrentHashMap<>();

    /**
     * find worker manager , then add event
     * @param event run event
     */
    @Override
    public void execute(IEvent event) {
        IWorkerManager workerManager = workerManagerMap.get(event.getWorkerClassName().toLowerCase());
        boolean result = workerManager.add(event);
        if (!result){
            event.onComplete(event,null);
        }
    }

    /**
     * register workerManager's worker
     * @param workerManager workerManager
     * @param workerClass worker
     */
    @Override
    public <T extends AbstractWorker> void register(IWorkerManager workerManager, Class<T> workerClass) {
        if (!workerManagerMap.containsKey(workerClass.getName())) {
            workerManagerMap.put(workerClass.getName().toLowerCase(), workerManager);
            workerManagerMap.put(workerClass.getSimpleName().toLowerCase(),workerManager);
        }
    }
}
