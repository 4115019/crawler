package ca.credits.deep;

import ca.credits.deep.scheduler.RabbimqDuplicateScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import com.google.common.util.concurrent.RateLimiter;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * Created by chenwen on 16/9/22.
 */
public class Bootstrap {
    public static EventController startTest(QueueInfo queueInfo, PageProcessor pageProcessor,RateLimiter rateLimiter){
        EventControlConfig config = null;

        EventController eventController = DefaultEventController.getInstance(config);

        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, pageProcessor,
                new RabbimqDuplicateScheduler(eventController).setDuplicateRemover(new HashSetDuplicateRemover())).rateLimiter(rateLimiter);

        eventController.add(queueInfo,rabbitSpider);

        return eventController;
    }

    public static EventController startTest(QueueInfo queueInfo, PageProcessor pageProcessor){
        return startTest(queueInfo,pageProcessor,RateLimiter.create(0.1));
    }
}
