package ca.credits.common.filter;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisBloomDuplicateFilter implements IDuplicateFilter {
    private final RBloomFilter<CharSequence> bloomFilter;

    private long expectedInsertions;

    private double fpp;

    private RAtomicLong counter;

    private RedissonClient redisson;

    private String name;

    public RedisBloomDuplicateFilter(String name, Redisson redisson){
        this.name = name;
        this.redisson = redisson;
        this.bloomFilter = redisson.getBloomFilter(name);
        this.counter = redisson.getAtomicLong(name);
        this.fpp = bloomFilter.getFalseProbability();
        this.expectedInsertions = bloomFilter.getExpectedInsertions();
    }

    public RedisBloomDuplicateFilter(int expectedInsertions, String name, RedissonClient redisson){
        this(expectedInsertions, 0.01, name, redisson);
    }

    public RedisBloomDuplicateFilter(int expectedInsertions, double fpp,String name, RedissonClient redisson){
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.name = name;
        this.redisson = redisson;

        RBloomFilter<CharSequence> bloomFilter = redisson.getBloomFilter(name);
        if (bloomFilter.isExists()){
            this.bloomFilter = bloomFilter;
        }else {
            this.bloomFilter = rebuildBloomFilter();
        }
    }

    protected RBloomFilter<CharSequence> rebuildBloomFilter() {
        counter = redisson.getAtomicLong(name);
        counter.set(0);

        RBloomFilter<CharSequence> bloomFilter = redisson.getBloomFilter(name);
        if (bloomFilter.isExists()){
            bloomFilter.delete();
            bloomFilter = redisson.getBloomFilter(name);
        }
        bloomFilter.tryInit(expectedInsertions,fpp);
        return bloomFilter;
    }

    @Override
    public boolean isDuplicate(String key) {
        boolean isDuplicate = bloomFilter.contains(key);
        if (!isDuplicate) {
            bloomFilter.add(key);
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    @Override
    public long size() {
        return counter.get();
    }

    @Override
    public void reset() {
        rebuildBloomFilter();
    }
}
