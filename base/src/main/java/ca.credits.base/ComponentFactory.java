package ca.credits.base;

import ca.credits.base.concurrent.ICountDownLatch;
import ca.credits.base.concurrent.StandaloneCountDownLatch;
import ca.credits.base.config.CrawlerConfigDefaults;
import ca.credits.base.event.IEvent;
import ca.credits.base.queue.IDuplicateKey;
import ca.credits.base.queue.IQueue;
import ca.credits.base.queue.StandaloneQueue;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chenwen on 16/9/2.
 */
public class ComponentFactory {
    /**
     * is or not distribute
     */
    public static final boolean distribute = CrawlerConfigDefaults.defaultDistribute();

    /**
     * get lock
     * @param key lock key
     * @return lock
     */
    public static Lock getLock(String key){
        if (distribute){
            return null;
        }else {
            return new ReentrantLock();
        }
    }

    public static Lock getLock(Class clz){
        return getLock(String.format("%s_%s",clz.getName(), UUID.randomUUID()));
    }

    /**
     * try CountDownLatch
     * @param count count
     * @return CountDownLatch
     */
    public static ICountDownLatch tryCountDownLatch(int count){
        if (distribute){
            return null;
        }else {
            return new StandaloneCountDownLatch(count);
        }
    }

    /**
     * create queue
     * @param name queue name
     * @param <T> queue type
     * @return queue
     */
    public static <T extends IDuplicateKey> IQueue<T> createQueue(String name){
        if (distribute){
            return null;
        }else {
            return new StandaloneQueue<>(name);
        }
    }


}
