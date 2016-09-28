package ca.credits.business.util;

import ca.credits.common.config.Config;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.impl.DefaultEventController;

/**
 * Created by huangpin on 16/9/27.
 */
public class EventControlUtil {
    public static EventController getEventController(){
        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));
        return DefaultEventController.getInstance(config);
    }
}
