package ca.credits.base.engine;

import ca.credits.base.ComponentFactory;
import ca.credits.base.balance.IBalance;
import ca.credits.base.balance.IBalanceManager;
import ca.credits.base.concurrent.ConcurrentLockException;
import ca.credits.base.concurrent.TryLockTimeoutException;
import ca.credits.base.event.IEvent;
import ca.credits.base.queue.IDuplicateKey;
import ca.credits.base.queue.IQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by chenwen on 16/8/30.
 */
@Slf4j
public abstract class AbstractWorkerManager<T extends IDuplicateKey,W extends AbstractWorker> implements IWorkerManager<T>{
    /**
     * the timeout,ms
     */
    protected long timeout = Long.MAX_VALUE;

    /**
     * the lockTimeUnit
     */
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * lock
     */
    protected Lock lock = ComponentFactory.getLock(this.getClass());

    /**
     * task queue
     */
    protected IQueue<T> queue;

    /**
     * workers
     */
    protected List<AbstractWorker> workers;

    /**
     * engine
     */
    protected final IEngine engine;

    /**
     * worker class
     */
    protected final Class<W> workerClass;

    /**
     * balance manager
     */
    protected IBalanceManager balanceManager;


    public AbstractWorkerManager(IEngine engine,IBalanceManager balanceManager,Class<W> workerClass,int workersCount){
        this.engine = engine;
        this.balanceManager = balanceManager;
        this.workerClass = workerClass;
        engine.register(this,workerClass);

        queue = ComponentFactory.createQueue(workerClass.getName());
        workers = new ArrayList<>();

        addWorkers(workersCount);
    }

    /**
     * add task
     * @param event event
     */
    @Override
    public boolean add(IEvent event){
        /**
         * step 1: balance manager
         */
        balanceManager.manage(this);

        /**
         * step 2: add manager
         */
        return addTask(event);
    }

    /**
     * add task
     * @param event event
     */
    protected abstract boolean addTask(IEvent event);

    /**
     * add worker
     * @return createWorker
     */
    protected abstract AbstractWorker createWorker();

    /**
     * worker is done , then remove worker from worker list
     * @param worker
     */
    @Override
    public void done(AbstractWorker worker) {
        workers.remove(worker);
    }

    /**
     * get least workers count
     * @return workers count
     */
    @Override
    public int getLeastWorkerCount() {
        return workers.size();
    }

    /**
     * get least task count
     * @return least task count
     */
    @Override
    public int getLeastTaskCount() {
        return queue.getLeastTask();
    }

    /**
     * add workers
     * @param count worker num
     */
    @Override
    public void addWorkers(int count) {
        try {
            if (lock.tryLock(timeout,timeUnit)) {
                for (int i = 0; i < count; ++i) {
                    AbstractWorker worker = createWorker();
                    worker.start();
                    workers.add(worker);
                }
            }else {
                log.error("get add worker lock timeout",new TryLockTimeoutException("get add worker lock timeout",timeout,timeUnit));
            }
        }catch (InterruptedException e) {
            log.error("get add worker lock failed",e);
        }finally {
            if (lock != null){
                lock.unlock();
            }
        }
    }

    /**
     * stop workers
     * @param count count
     */
    @Override
    public void stopWorkers(int count) {
        try {
            if (lock.tryLock(timeout,timeUnit)) {
                if (getLeastWorkerCount() >= count) {
                    for (int i = 0; i < count; ++i) {
                        if (getLeastWorkerCount() > 0) {
                            workers.get(0).shutdown();
                        }
                    }
                }
            }else {
                log.error("get stop worker lock timeout",new TryLockTimeoutException("get stop worker lock timeout",timeout,timeUnit));
            }
        }catch (InterruptedException e) {
            log.error("get stop worker lock failed",e);
        }finally {
            if (lock != null){
                lock.unlock();
            }
        }
    }

    /**
     * take task
     * @return task
     * @throws InterruptedException exception
     */
    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }
}
