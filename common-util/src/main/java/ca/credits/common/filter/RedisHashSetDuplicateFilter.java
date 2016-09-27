package ca.credits.common.filter;

import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisHashSetDuplicateFilter implements IDuplicateFilter {
    private RedissonClient redisson;

    private String name;

    private RSet<String> keys;

    public RedisHashSetDuplicateFilter(String name, RedissonClient redisson){
        this.redisson = redisson;
        this.name = name;
        this.keys = redisson.getSet(name);
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
