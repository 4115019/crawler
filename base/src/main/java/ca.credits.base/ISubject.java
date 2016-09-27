package ca.credits.base;

/**
 * Created by chenwen on 16/8/26.
 */
public interface ISubject {
    /**
     * register listener
     * @param listener listener
     */
    void registerListener(IListener listener);

    /**
     * remove listener
     * @param listener listener
     */
    void removeListener(IListener listener);


    /**
     * remove all listener
     */
    void removeAll();
}
