package ca.credits.base.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenwen on 16/8/30.
 *
 * 这是一个同步锁接口,改锁会有一个count字段,当count为0是将会释放锁
 */
public interface ICountDownLatch {
    /**
     * count - 1
     */
    void countDown();

    /**
     * 等待count为0,或者超时
     * @param timeout times
     * @param unit lockTime unit
     * @throws InterruptedException interrupted exception
     */
    void await(long timeout, TimeUnit unit)throws InterruptedException;

    /**
     * 永久等待,或者被中断
     * @throws InterruptedException 中断异常
     */
    void await()throws InterruptedException;

    /**
     * 获取count值
     * @return count
     */
    long getCount();
}
