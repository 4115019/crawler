package ca.credits.base.event;

import ca.credits.base.IExecutive;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IEvent extends IExecutive {
    /**
     * get worker class name
     * @return worker class name
     */
    String getWorkerClassName();
}
