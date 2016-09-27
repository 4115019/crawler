package ca.credits.queue;

import ca.credits.queue.impl.DefaultEventController;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chenwen on 16/9/20.
 */
@Slf4j
public class EventTemplateTest {

    private String defaultHost = "121.42.147.36";

    private EventController controller;

    private EventTemplate eventTemplate;

    private QueueInfo queueInfo = QueueInfo.builder().queueName("chenwen").exchangeName("chenwen").build();

    @Before
    public void init() throws IOException {
        EventControlConfig config = new EventControlConfig(defaultHost);
        config.setUsername("wecash-admin");
        config.setPassword("wecash2015");
        controller = DefaultEventController.getInstance(config);
        eventTemplate = controller.getEventTemplate();
        controller.add(queueInfo,new RequestProcessor());
        controller.start();

        log.info("end");
    }

    @Test
    public void testSend() throws SendRefuseException {
        eventTemplate.send(queueInfo,new RequestMessage("http://www.baidu.com"));
        eventTemplate.send(queueInfo,new RequestMessage("http://www.baidu.com"));
        log.info("end");
    }

    @After
    public void end() throws InterruptedException{
        Thread.sleep(5000);
    }
}