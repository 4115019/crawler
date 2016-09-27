package ca.credits.business.pipeline;

import ca.credits.business.util.DynamoDBUtil;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by huangpin on 16/9/27.
 */
public class DynamodbPipeline implements Pipeline {
    private String tableName;

    public DynamodbPipeline(String tableName){
        this.tableName = tableName;
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems != null && CollectionUtils.isNotEmpty(resultItems.getTemplates())){
            DynamoDBUtil.save(resultItems.getTemplates(),tableName);
        }
    }
}
