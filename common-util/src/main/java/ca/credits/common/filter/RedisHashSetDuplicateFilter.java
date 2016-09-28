package ca.credits.common.filter;

import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisHashSetDuplicateFilter implements IDuplicateFilter {
    private RSet<String> keys;

    public RedisHashSetDuplicateFilter(String name, RedissonClient redisson){
        this.keys = redisson.getSet(name);
        this.keys.expire(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    @Override
    public boolean isDuplicate(String key) {
        return !keys.add(key);
    }

    @Override
    public long size() {
        return keys.size();
    }

    @Override
    public void reset() {
        keys.clear();
    }
}
