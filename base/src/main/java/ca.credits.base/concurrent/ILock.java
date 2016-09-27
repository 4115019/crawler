package ca.credits.base.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenwen on 16/9/2.
 */
public interface ILock {
    /**
     * start lock
     * @param time lock lockTime
     * @param timeUnit lock unit
     */
    void lock(long time, TimeUnit timeUnit);

    /**
     * try get lock
     * @param time get lock lockTime
     * @param unit get lock lockTime unit
     * @return is or not get lock
     * @throws InterruptedException
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

}
