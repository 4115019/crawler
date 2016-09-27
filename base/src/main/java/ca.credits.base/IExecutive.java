package ca.credits.base;

import ca.credits.base.diagram.AbstractNode;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IExecutive extends ISubject,IListener{
    /**
     * start run
     */
    void run();
    /**
     * get activity id
     * @return activity id
     */
    String getActivityId();

    /**
     * get task id
     * @return task id
     */
    String getTaskId();

    /**
     * get id
     * @return id
     */
    String getId();

    /**
     * get status
     * @return status
     */
    Status getStatus();

    /**
     * get node
     *
     * @return node
     */
    AbstractNode getNode();

    /**
     * executive is complete
     * @return
     */
    boolean isComplete();

    /**
     * get child random id
     */
    String getRandomChildId();

    /**
     * regulator status
     */
    enum Status{
        /**
         * regulator not start run
         */
        UNDO,
        /**
         * regulator running
         */
        RUNNING,
        /**
         * regulator complete success
         */
        DONE,
        /**
         * regulator complete exception
         */
        EXCEPTION;

        public static Status getValue(int i){
            for(Status status : values()){
                if (status.ordinal() == i){
                    return status;
                }
            }
            return null;
        }
    }
}
