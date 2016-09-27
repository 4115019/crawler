package ca.credits.base;

import ca.credits.base.diagram.*;
import ca.credits.base.engine.LoggerWorker;
import ca.credits.base.task.DefaultTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chenwen on 16/8/29.
 */
@Slf4j
public class DefaultTaskTest{
    /**
     *
     * <activity id="loggerEngineTest">
     *      <task id="task1">
     *          <event id="task1.startEvent" next="task1.event1">
     *          </event>
     *
     *          <event id="task1.event1" next="task2,task3">
     *          </event>
     *
     *          <task id="task2" next="task1.endEvent">
     *              <event id="task2.startEvent" next="task2.event1">
     *              </event>
     *
     *              <event id="task2.event1" next="task2.endEvent">
     *              </event>
     *
     *              <event id="task2.endEvent" next="end">
     *              </event>
     *          </task>
     *
     *          <task id="task3" next="task1.endEvent">
     *              <event id="task3.startEvent" next="task3.event1">
     *              </event>
     *
     *              <event id="task3.event1" next="task3.endEvent">
     *              </event>
     *
     *              <event id="task3.endEvent" next="end">
     *              </event>
     *          </task>
     *
     *          <event id="task1.endEvent" next="end">
     *          </event>
     *      </task>
     * </activity>
     *
     *
     */
    @Test
    public void testLoggerEngine() throws InterruptedException {
        /**
         * create task1 nodes
         */
        AbstractNode task1StartEvent = DefaultEventNode.create("task1.startEvent", LoggerWorker.class.getName());
        AbstractNode task1Event1 = DefaultEventNode.create("task1.event1", LoggerWorker.class.getName());
        AbstractNode task1EndEvent = DefaultEventNode.create("task1.endEvent", LoggerWorker.class.getName());

        /**
         * create task2 nodes
         */
        AbstractNode task2StartEvent = DefaultEventNode.create("task2.startEvent", LoggerWorker.class.getName());
        AbstractNode task2Event1 = DefaultEventNode.create("task2.event1", LoggerWorker.class.getName());
        AbstractNode task2EndEvent = DefaultEventNode.create("task2.endEvent", LoggerWorker.class.getName());


        /**
         * create task3 nodes
         */
        AbstractNode task3StartEvent = DefaultEventNode.create("task3.startEvent", LoggerWorker.class.getName());
        AbstractNode task3Event1 = DefaultEventNode.create("task3.event1", LoggerWorker.class.getName());
        AbstractNode task3EndEvent = DefaultEventNode.create("task3.endEvent", LoggerWorker.class.getName());

        /**
         * create task2 edges
         */
        Edge task2Edge1 = Edge.builder().id("task2Edge1").source(task2StartEvent).target(task2Event1).build();
        Edge task2Edge2 = Edge.builder().id("task2Edge2").source(task2Event1).target(task2EndEvent).build();

        /**
         * create task2
         */
        AbstractNode task2Node = DefaultTaskNode.create("task2",DAG.create().addEdges(task2Edge1,task2Edge2));

        /**
         * create task3 edges
         */
        Edge task3Edge1 = Edge.builder().id("task3Edge1").source(task3StartEvent).target(task3Event1).build();
        Edge task3Edge2 = Edge.builder().id("task3Edge2").source(task3Event1).target(task3EndEvent).build();

        /**
         * create task3
         */
        AbstractNode task3Node = DefaultTaskNode.create("task3",DAG.create().addEdges(task3Edge1,task3Edge2));

        /**
         * create task1 edges
         */
        Edge task1Edge1 = Edge.builder().id("task1Edge1").source(task1StartEvent).target(task1Event1).build();
        Edge task1Edge2 = Edge.builder().id("task1Edge2").source(task1Event1).target(task2Node).build();
        Edge task1Edge3 = Edge.builder().id("task1Edge3").source(task1Event1).target(task3Node).build();
        Edge task1Edge4 = Edge.builder().id("task1Edge4").source(task2Node).target(task1EndEvent).build();
        Edge task1Edge5 = Edge.builder().id("task1Edge5").source(task3Node).target(task1EndEvent).build();

        /**
         * create task1
         */
        AbstractNode task1Node = DefaultTaskNode.create("task1",DAG.create().addEdges(task1Edge1,task1Edge2,task1Edge3,task1Edge4,task1Edge5));

        IExecutive executive = new DefaultTask("testLoggerEngine",task1Node,null);

        executive.run();
    }

    /**
     * 多事件并发
     * <activity id="loggerEngineTest">
     *      <task id="task1">
     *          <event id="task1.event1" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.event2" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.event3" next="task1.endEvent">
     *          </event>
     *
     *          <event id="task1.endEvent" next="end">
     *          </event>
     *      </task>
     * </activity>
     *
     *
     */
    @Test
    public void testConcurrentLoggerEngine() throws InterruptedException {
        /**
         * create task1 nodes
         */
        AbstractNode task1StartEvent = DefaultEventNode.create("task1.startEvent", LoggerWorker.class.getName());
        AbstractNode task1Event1 = DefaultEventNode.create("task1.event1", LoggerWorker.class.getName());
        AbstractNode task1Event2 = DefaultEventNode.create("task1.event2", LoggerWorker.class.getName());
        AbstractNode task1Event3 = DefaultEventNode.create("task1.event3", LoggerWorker.class.getName());
        AbstractNode task1EndEvent = DefaultEventNode.create("task1.endEvent", LoggerWorker.class.getName());

        /**
         * create task1 edges
         */
        Edge task1Edge1 = Edge.builder().id("task1Edge1").source(task1StartEvent).target(task1Event1).build();
        Edge task1Edge2 = Edge.builder().id("task1Edge2").source(task1StartEvent).target(task1Event2).build();
        Edge task1Edge3 = Edge.builder().id("task1Edge3").source(task1StartEvent).target(task1Event3).build();
        Edge task1Edge4 = Edge.builder().id("task1Edge4").source(task1Event1).target(task1EndEvent).build();
        Edge task1Edge5 = Edge.builder().id("task1Edge5").source(task1Event2).target(task1EndEvent).build();
        Edge task1Edge6 = Edge.builder().id("task1Edge6").source(task1Event3).target(task1EndEvent).build();

        AbstractNode task1 = DefaultTaskNode.create("task1",DAG.create().addEdges(task1Edge1,task1Edge2,task1Edge3,task1Edge4,task1Edge5,task1Edge6));

        IExecutive executive = new DefaultTask("testConcurrentLoggerEngine",task1,null);

        executive.run();
    }
}
