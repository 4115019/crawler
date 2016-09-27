package ca.credits.queue;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenwen on 16/9/20.
 */
@Slf4j
public class RequestProcessor implements EventProcessor<RequestMessage>{

    @Getter
    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void process(RequestMessage message) throws Exception {
        log.info("");

        log.info(message.getUrl());
        log.info(message.get_id());

        log.info(counter.incrementAndGet() + "");

        assert Objects.equals(message.getUrl(), "http://www.baidu.com");
    }
}
