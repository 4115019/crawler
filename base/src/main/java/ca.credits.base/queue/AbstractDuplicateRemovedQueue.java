package ca.credits.base.queue;

/**
 * Created by chenwen on 16/9/18.
 */
public abstract class AbstractDuplicateRemovedQueue<T extends IDuplicateKey> implements IQueue<T>{
    private IDuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public IDuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public AbstractDuplicateRemovedQueue setDuplicateRemover(IDuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public boolean put(T t) throws InterruptedException {
        final String key = t.getDuplicateKey();
        if (key == null || !duplicatedRemover.isDuplicate(key)){
            pushWhenNoDuplicate(t);
            return true;
        }
        return false;
    }

    public abstract void pushWhenNoDuplicate(T t);
}
