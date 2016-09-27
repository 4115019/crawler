package ca.credits.deep;

import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.impl.DefaultEventController;

/**
 * Created by chenwen on 16/9/22.
 */
public class Bootstrap {
    public static EventController eventController;

    static {
        EventControlConfig config = new EventControlConfig("open-test.wecash.net");
        config.setUsername("wecash-admin");
        config.setPassword("wecash2015");
        eventController = DefaultEventController.getInstance(config);
    }
}
