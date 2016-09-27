package ca.credits.base.queue;

/**
 * Created by chenwen on 16/8/30.
 */
public interface IQueue<T> {
    /**
     * get task from queue
     * @return task
     */
    T take()throws InterruptedException;

    /**
     * push task to queue
     * @param t task
     */
    boolean put(T t)throws InterruptedException;

    /**
     * get least task
     * @return count
     */
    int getLeastTask();

    /**
     * get queue name
     * @return name
     */
    String getName();
}
