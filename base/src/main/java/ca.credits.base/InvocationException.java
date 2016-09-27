package ca.credits.base;

/**
 * Created by chenwen on 16/9/6.
 *
 * java invocation exception
 *
 * method not found or class not found
 */
public class InvocationException extends RuntimeException {
    public InvocationException(String msg,Throwable e){
        super(msg,e);
    }
}
