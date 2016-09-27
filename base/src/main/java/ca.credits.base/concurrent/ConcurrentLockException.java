package ca.credits.base.concurrent;

/**
 * Created by chenwen on 16/9/5.
 *
 * try lock exception
 *
 */
public class ConcurrentLockException extends RuntimeException {
    public ConcurrentLockException(String msg){
        super(msg);
    }

    public ConcurrentLockException(String msg,Throwable e){
        super(msg,e);
    }
}
