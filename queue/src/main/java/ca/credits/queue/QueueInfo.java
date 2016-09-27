package ca.credits.queue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by chenwen on 16/9/20.
 */
@Builder
public class QueueInfo implements Serializable{
    /**
     * serial version
     */
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String queueName;

    @Getter
    @Setter
    private String exchangeName;

    @Setter
    private ExchangeEnum exchangeType = ExchangeEnum.DIRECT;

    public ExchangeEnum getExchangeType(){
        return exchangeType = exchangeType == null ? ExchangeEnum.DIRECT : exchangeType;
    }
}
