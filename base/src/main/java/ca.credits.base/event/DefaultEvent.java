package ca.credits.base.event;

import ca.credits.base.diagram.AbstractEventNode;
import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.engine.LoggerWorker;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.IExecutive;
import ca.credits.base.task.ITask;

import java.util.List;

/**
 * Created by chenwen on 16/8/29.
 */
public class DefaultEvent extends AbstractEvent {
    public DefaultEvent(AbstractEventNode node, ITask regulator){
        super(node.getWorkerClass(),regulator.getActivityId(),node,regulator);
    }
}
