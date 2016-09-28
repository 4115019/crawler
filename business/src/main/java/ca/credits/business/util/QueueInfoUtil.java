package ca.credits.business.util;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;

/**
 * Created by huangpin on 16/9/27.
 */
public class QueueInfoUtil {
    public static QueueInfo getQueueInfo(PlatformCodeEnum.INameCode nameCode){
        return QueueInfo.builder().queueName(nameCode.getCode()).exchangeName(nameCode.getCode()).exchangeType(ExchangeEnum.DIRECT).build();
    }
}
