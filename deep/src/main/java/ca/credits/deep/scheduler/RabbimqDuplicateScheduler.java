package ca.credits.deep.scheduler;

import ca.credits.queue.EventController;
import ca.credits.queue.SendRefuseException;
import org.apache.http.annotation.ThreadSafe;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.PushFailedException;

/**
 * Created by chenwen on 16/9/20.
 */
@ThreadSafe
public class RabbimqDuplicateScheduler extends DuplicateRemovedScheduler {
    private EventController eventController;

    public RabbimqDuplicateScheduler(EventController eventController){
        this.eventController = eventController;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) throws PushFailedException {
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
