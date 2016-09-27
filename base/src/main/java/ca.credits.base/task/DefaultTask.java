package ca.credits.base.task;

import ca.credits.base.*;
import ca.credits.base.concurrent.StandaloneCountDownLatch;
import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.diagram.AbstractTaskNode;
import ca.credits.base.diagram.DefaultTaskNode;
import ca.credits.base.event.IEvent;
import ca.credits.base.gateway.DefaultGateway;

import java.util.List;

/**
 * Created by chenwen on 16/8/29.
 */
public class DefaultTask extends AbstractTask {
    public DefaultTask(String activityId, AbstractNode node, IExecutiveManager regulator) {
        super(activityId, node , regulator);
    }
}
