package ca.credits.queue.impl;

import ca.credits.common.serialize.CodecFactory;
import ca.credits.common.serialize.HessionCodecFactory;
import ca.credits.queue.*;
import ca.credits.queue.util.ValidateUtil;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.IOException;

/**
 * Created by chenwen on 16/9/20.
 */
public class DefaultEventTemplate implements EventTemplate {
    private EventController eventController;

    private AmqpTemplate amqpTemplate;

    private CodecFactory codecFactory;

    public DefaultEventTemplate(AmqpTemplate amqpTemplate, EventController eventController, CodecFactory codecFactory){
        this.amqpTemplate = amqpTemplate;
        this.eventController = eventController;
        this.codecFactory = codecFactory == null ? new HessionCodecFactory() : codecFactory;
    }

    public <T extends Message> void send(QueueInfo queueInfo, T message)  throws SendRefuseException {
        if (!ValidateUtil.validateQueueInfo(queueInfo)){
            throw new SendRefuseException("illegal queue info");
        }

        if (!eventController.beBinded(queueInfo)){
            eventController.declareBinding(queueInfo);
        }

        try {
            EventMessage eventMessage = new EventMessage(queueInfo,codecFactory.serialize(message));
            amqpTemplate.convertAndSend(queueInfo.getExchangeName(),queueInfo.getQueueName(),eventMessage);
        }catch (AmqpException | IOException e){
            throw new SendRefuseException("send event fail",e);
        }
    }
}
