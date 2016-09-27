package ca.credits.base.engine;

import ca.credits.base.event.DefaultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * Created by chenwen on 16/8/30.
 */
@Slf4j
public class LoggerWorker extends AbstractWorker<DefaultEvent> {
    /**
     * create worker with workerManager
     *
     * @param workerManager workManager
     */
    public LoggerWorker(IWorkerManager<DefaultEvent> workerManager) {
        super(workerManager);
    }

    @Override
    protected void doWork(DefaultEvent task) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        log.info(String.format("start run event activityId = %s, taskId = %s, id = %s", task.getActivityId(), task.getTaskId(), task.getId()));
        task.onStart(task, null);
        try {
            log.info(String.format("complete event activityId = %s, taskId = %s, id = %s", task.getActivityId(), task.getTaskId(), task.getId()));
//            sleep(5000);
            task.onComplete(task, null);
        } catch (Exception e) {
            log.info(String.format("exception for event activityId = %s, taskId = %s, id = %s", task.getActivityId(), task.getTaskId(), task.getId()), e);
            task.onThrowable(task, e, null);
        } finally {
            stopWatch.stop();
//            log.info(String.format("finally for event activityId = %s, taskId = %s, id = %s costTime = %s (ms)", task.getActivityId(), task.getTaskId(), task.getId(), stopWatch.getTotalTimeMillis()));
        }
    }
}
