package ca.credits.base.engine;

import ca.credits.base.ComponentFactory;
import ca.credits.base.concurrent.ICountDownLatch;
import ca.credits.base.concurrent.StandaloneCountDownLatch;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by chenwen on 16/8/30.
 */
@Slf4j
public abstract class AbstractWorker<T> extends Thread {
    /**
     * work manager
     */
    protected IWorkerManager<T> workerManager;

    /**
     * worker status
     */
    protected Status status;

    /**
     * count down latch
     */
    private ICountDownLatch countDownLatch;

    /**
     * create worker with workerManager
     * @param workerManager workManager
     */
    public AbstractWorker(IWorkerManager<T> workerManager){
        this.workerManager = workerManager;
        this.status = Status.WAITING;
        this.countDownLatch = ComponentFactory.tryCountDownLatch(1);
    }

    /**
     * really do work
     * @param task task
     */
    protected abstract void doWork(T task);

    /**
     * start run worker
     */
    @Override
    public void run(){
        /**
         * status is not stopped and thread is not interrupted
         */
        while (status != Status.STOP && !this.isInterrupted()){
            T task;
            try{
                status = Status.WAITING;
                task = workerManager.take();
            }catch (InterruptedException e) {
                break;
            }

            /**
             * if task is not null , then running
             */
            if (task != null){
                status = Status.RUNNING;
                doWork(task);
            }
        }

        /**
         * when worker is stop, count down
         */
        countDownLatch.countDown();

        /**
         * when worker exit run,worker is done
         */
        status = Status.STOP;
        workerManager.done(this);
    }

    /**
     * stop worker
     */
    public void shutdown(){
        status = Status.STOP;

        this.interrupt();

        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            log.error("worker is shutdown, then throw interrupted exception",e);
        }
    }

    /**
     * worker status
     */
    public enum Status{
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 停止
         */
        STOP,
        /**
         * 等待
         */
        WAITING;

    }
}
