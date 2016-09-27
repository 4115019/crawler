package ca.credits.queue;

/**
 * Created by chenwen on 16/9/20.
 */
public interface EventProcessor<T extends Message> {
    /**
     * recv message and deal message
     * @param message message
     * @throws Exception exception
     */
    void process(T message)throws Exception;
}
