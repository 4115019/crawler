package ca.credits.engine.downloader;

import ca.credits.base.balance.IBalanceManager;
import ca.credits.base.engine.AbstractWorker;
import ca.credits.base.engine.AbstractWorkerManager;
import ca.credits.base.engine.IEngine;
import ca.credits.base.event.IEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by chenwen on 16/8/31.
 */
@Named("asyncHttpClientWorkerManager")
@Singleton
public class AsyncHttpClientWorkerManager extends AbstractWorkerManager<AsyncHttpClientRequest,AsyncHttpClientWorker>{
    @Inject
    public AsyncHttpClientWorkerManager(IEngine engine, IBalanceManager balanceManager){
        super(engine,balanceManager,AsyncHttpClientWorker.class,2);
    }

    @Override
    protected boolean addTask(IEvent event) {
        try {
            return queue.put(new AsyncHttpClientRequest(event));
        } catch (InterruptedException e) {
            event.onThrowable(event,e,null);
        }
        return false;
    }

    @Override
    protected AbstractWorker createWorker() {
        return new AsyncHttpClientWorker(this);
    }
}
