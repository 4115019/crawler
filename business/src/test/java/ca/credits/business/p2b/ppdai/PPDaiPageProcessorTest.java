package ca.credits.business.p2b.ppdai;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.util.EventControlUtil;
import ca.credits.common.filter.RedisHashSetDuplicateFilter;
import ca.credits.common.util.RedissonUtil;
import ca.credits.deep.scheduler.RabbitmqDuplicateScheduler;
import ca.credits.deep.scheduler.RedisDuplicateRemover;
import ca.credits.queue.EventController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.redisson.api.RSet;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * Created by chenwen on 16/9/28.
 */
@Slf4j
public class PPDaiPageProcessorTest {

    @Test
    public void testMain() throws Exception {
//        EventController eventController = EventControlUtil.getEventController();
////        eventController.getEventTemplate().send(QueueInfoUtil.getQueueInfo(PlatformCodeEnum.P2B.PPDAI),new Request("http://www.ppdai.com/blacklist/2016_m0_p21"));
//        Scheduler scheduler = new RabbitmqDuplicateScheduler(eventController)
//                .setDuplicateRemover(
//                        new RedisDuplicateRemover(
//                                new RedisHashSetDuplicateFilter(PlatformCodeEnum.P2B.PPDAI.getCode(),
//                                        RedissonUtil.getInstance().getRedisson())));
//        scheduler.push(new Request("http://www.ppdai.com/blacklistdetail/pdu6372601272"),null);

        RSet<String> set = RedissonUtil.getInstance().getRedisson().getSet(PlatformCodeEnum.P2B.PPDAI.getCode());

        set.add("2");
        log.info(set.size() + "");


    }
}