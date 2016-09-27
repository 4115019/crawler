package ca.credits.base.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenwen on 16/9/5.
 *
 * try lock exception
 *
 */
public class TryLockTimeoutException extends RuntimeException {
    public TryLockTimeoutException(String msg ,long time , TimeUnit timeUnit){
        super(String.format("%s timeout = %s(ms)",msg,timeUnit.toMillis(time)));
    }

    public TryLockTimeoutException(String msg,long time,TimeUnit timeUnit, Throwable e){
        super(msg,e);
    }
}
