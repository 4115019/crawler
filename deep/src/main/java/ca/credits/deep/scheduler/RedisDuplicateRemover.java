package ca.credits.deep.scheduler;

import ca.credits.common.filter.IDuplicateFilter;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

/**
 * Created by chenwen on 16/9/20.
 */
public class RedisDuplicateRemover implements DuplicateRemover {
    private IDuplicateFilter duplicateFilter;

    public RedisDuplicateRemover(IDuplicateFilter duplicateFilter){
        this.duplicateFilter = duplicateFilter;
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return this.duplicateFilter.isDuplicate(request.getUrl());
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        this.duplicateFilter.reset();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return (int) this.duplicateFilter.size();
    }
}
