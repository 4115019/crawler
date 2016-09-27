package ca.credits.base.queue;

/**
 * Created by chenwen on 16/9/18.
 */
public interface IDuplicateRemover {
    boolean isDuplicate(final String key);
}
