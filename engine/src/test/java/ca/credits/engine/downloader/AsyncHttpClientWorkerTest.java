package ca.credits.engine.downloader;

import ca.credits.base.IExecutive;
import ca.credits.base.diagram.*;
import ca.credits.base.engine.LoggerWorker;
import ca.credits.base.task.DefaultTask;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by chenwen on 16/9/6.
 */
public class AsyncHttpClientWorkerTest {

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
     */
    @Test
    public void testDoWork() throws Exception {
        /**
         * create task1 nodes
         */
        AbstractNode task1StartEvent = DefaultEventNode.create("task1.startEvent", LoggerWorker.class.getName());

        AbstractNode task1Event1 = DefaultEventNode.create("task1.event1", AsyncHttpClientWorker.class.getName())
                .property("url","http://www.ppdai.com/blacklist").property("method","GET");

        AbstractNode task1Event2 = DefaultEventNode.create("task1.event2", AsyncHttpClientWorker.class.getName())
                .property("url","http://www.ppdai.com/blacklist")
                .property("method","GET");

        AbstractNode task1Event3 = DefaultEventNode.create("task1.event3", AsyncHttpClientWorker.class.getName())
                .property("url","http://www.ppdai.com/blacklist")
                .property("method","GET");

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

        AbstractNode task1 = DefaultTaskNode.create("task1", DAG.create().addEdges(task1Edge1,task1Edge2,task1Edge3,task1Edge4,task1Edge5,task1Edge6));

        IExecutive executive = new DefaultTask("testAsyncHttpClientWorker",task1,null);

        executive.run();
    }
}