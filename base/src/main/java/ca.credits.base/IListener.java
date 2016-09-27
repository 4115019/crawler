package ca.credits.base;

import ca.credits.common.Properties;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IListener{
    /**
     * when event start run
     */
    void onStart(ISubject subject, Properties args);

    /**
     * when event success run
     */
    void onComplete(ISubject subject, Properties args);

    /**
     * when throw Exception on event running
     */
    void onThrowable(ISubject subject, Throwable throwable, Properties args);
}
