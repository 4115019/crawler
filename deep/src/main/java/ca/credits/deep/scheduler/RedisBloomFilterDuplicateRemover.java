package ca.credits.deep.scheduler;

import ca.credits.common.filter.IDuplicateFilter;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisBloomFilterDuplicateRemover implements DuplicateRemover {
    private IDuplicateFilter redisBloomDuplicateFilter;

    public RedisBloomFilterDuplicateRemover(IDuplicateFilter redisBloomDuplicateFilter){
        this.redisBloomDuplicateFilter = redisBloomDuplicateFilter;
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return this.redisBloomDuplicateFilter.isDuplicate(request.getUrl());
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        this.redisBloomDuplicateFilter.reset();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return (int) this.redisBloomDuplicateFilter.size();
    }
}
