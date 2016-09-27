package ca.credits.base.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenwen on 16/8/30.
 * this is standalone count down latch
 */
public class StandaloneCountDownLatch implements ICountDownLatch {
    private CountDownLatch countDownLatch;

    public StandaloneCountDownLatch(int count){
        countDownLatch = new CountDownLatch(count);
    }

    @Override
    public void countDown() {
        countDownLatch.countDown();
    }

    @Override
    public void await(long timeout, TimeUnit unit) throws InterruptedException {
        countDownLatch.await(timeout,unit);
    }

    @Override
    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    @Override
    public long getCount() {
        return countDownLatch.getCount();
    }
}
