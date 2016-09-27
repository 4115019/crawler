package ca.credits.engine.downloader;

import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.IWorkerManager;
import ca.credits.base.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

import java.nio.charset.Charset;

/**
 * Created by chenwen on 16/8/31.
 */
@Slf4j
public class AsyncHttpClientWorker extends AbstractWorker<AsyncHttpClientRequest> {
    private AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
    /**
     * create worker with workerManager
     * @param workerManager workManager
     */
    public AsyncHttpClientWorker(IWorkerManager<AsyncHttpClientRequest> workerManager) {
        super(workerManager);
    }

    /**
     * do work
     * @param task task
     */
    @Override
    protected void doWork(AsyncHttpClientRequest task) {
        if (asyncHttpClient.isClosed()){
            asyncHttpClient = new DefaultAsyncHttpClient();
        }

        task.getEvent().onStart(task.getEvent(),null);


        log.info("start download url = {}",task.getRequest().getUrl());

        asyncHttpClient.executeRequest(task.getRequest(), new AsyncCompletionHandler<Response>(){
            @Override
            public Response onCompleted(Response response) throws Exception{
                try {
                    AsyncHttpClientResponse asyncHttpClientResponse = new AsyncHttpClientResponse(task, response);

                    if (task.getResponseProcessor() != null) {
                        task.getResponseProcessor().process(asyncHttpClientResponse);
                    }

                    task.getEvent().onComplete(task.getEvent(), asyncHttpClientResponse.getProperties());
                }catch (Exception e){
                    task.getEvent().onThrowable(task.getEvent(),e,null);
                }
                return response;
            }

            @Override
            public void onThrowable(Throwable t){
                task.getEvent().onThrowable(task.getEvent(),t,null);
            }
        });
    }
}
