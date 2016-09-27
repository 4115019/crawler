package ca.credits.queue.util;

import ca.credits.queue.QueueInfo;

/**
 * Created by chenwen on 16/9/20.
 */
public class ValidateUtil {
    public static boolean validateQueueInfo(QueueInfo queueInfo){
        return queueInfo != null && queueInfo.getQueueName() != null && queueInfo.getExchangeName() != null && queueInfo.getExchangeType() != null;
    }
}
