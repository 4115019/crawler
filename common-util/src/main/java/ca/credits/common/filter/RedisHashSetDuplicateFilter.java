package ca.credits.common.filter;

import org.redisson.Redisson;
import org.redisson.api.RSet;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisHashSetDuplicateFilter implements IDuplicateFilter {
    private Redisson redisson;

    private String name;

    private RSet<String> keys;

    public RedisHashSetDuplicateFilter(String name, Redisson redisson){
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
