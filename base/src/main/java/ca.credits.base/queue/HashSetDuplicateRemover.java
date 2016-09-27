package ca.credits.base.queue;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author code4crafer@gmail.com
 */
public class HashSetDuplicateRemover implements IDuplicateRemover {

    private Set<String> urls = Sets.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public boolean isDuplicate(String key) {
        return !urls.add(key);
    }
}
