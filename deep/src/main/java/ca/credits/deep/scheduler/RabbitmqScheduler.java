package ca.credits.deep.scheduler;

import ca.credits.queue.EventController;
import ca.credits.queue.SendRefuseException;
import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * Created by chenwen on 16/9/20.
 */
@ThreadSafe
public class RabbitmqScheduler implements Scheduler{
    private EventController eventController;

    public RabbitmqScheduler(EventController eventController){
        this.eventController = eventController;
    }

    @Override
    public void push(Request request, Task task) throws PushFailedException {
        try {
            this.eventController.getEventTemplate().send(task.getQueueInfo(),request);
        } catch (SendRefuseException e) {
            throw new PushFailedException("push requeust failed",e);
        }
    }

    @Override
    public Request poll(Task task) {
        return null;
    }
}
