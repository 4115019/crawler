package ca.credits.engine;

import ca.credits.base.IExecutive;
import ca.credits.base.diagram.*;
import ca.credits.base.engine.LoggerWorker;
import ca.credits.base.kit.Constants;
import ca.credits.base.task.DefaultTask;
import ca.credits.engine.downloader.AsyncHttpClientWorker;
import ca.credits.engine.parser.IResponseProcessor;
import ca.credits.engine.parser.sample.PPDaiPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

/**
 * Created by chenwen on 16/9/18.
 */
@Slf4j
public class IResponseProcessorTest {
    @Test
    public void testProcess() throws Exception {
        /**
         * create task1 nodes
         */
        AbstractNode task1StartEvent = DefaultEventNode.create("task1.startEvent", LoggerWorker.class.getName());

        AbstractNode task1Event1 = DefaultEventNode.create("task1.event1", AsyncHttpClientWorker.class.getName())
                .property(Constants.RequestParams.URL,"http://www.ppdai.com/blacklist")
                .property(Constants.RequestParams.METHOD,"GET")
                .property(Constants.RESPONSE_PROCESSOR,PPDaiPageProcessor.class.getName())
                .property(Constants.RequestParams.IS_RANDOM_USER_AGET,true);

        AbstractNode task1EndEvent = DefaultEventNode.create("task1.endEvent", LoggerWorker.class.getName());

        /**
         * create task1 edges
         */
        Edge task1Edge1 = Edge.builder().id("task1Edge1").source(task1StartEvent).target(task1Event1).build();
        Edge task1Edge2 = Edge.builder().id("task1Edge2").source(task1Event1).target(task1EndEvent).build();

        AbstractNode task1 = DefaultTaskNode.create("task1", DAG.create().addEdges(task1Edge1,task1Edge2));

        IExecutive executive = new DefaultTask("testAsyncHttpClientWorker",task1,null);

        executive.run();
    }
}