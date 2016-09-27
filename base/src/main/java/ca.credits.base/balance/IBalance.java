package ca.credits.base.balance;

/**
 * Created by chenwen on 16/9/6.
 */
public interface IBalance {
    /**
     * get least worker
     * @return least worker count
     */
    int getLeastWorkerCount();

    /**
     * get least task count
     */
    int getLeastTaskCount();

    /**
     * add count worker
     * @param count worker num
     */
    void addWorkers(int count);

    /**
     * reduce count worker
     * @param count count
     */
    void stopWorkers(int count);
}
