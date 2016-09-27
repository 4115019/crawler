package ca.credits.base.diagram;

import ca.credits.base.task.DefaultTask;

/**
 * Created by chenwen on 16/9/1.
 */
public class DefaultTaskNode extends AbstractTaskNode{
    /**
     * the creator
     * @param id id
     * @param dag dag
     * @return
     */
    public static DefaultTaskNode create(String id,DAG dag){
        return new DefaultTaskNode(id,dag,false);
    }

    public static DefaultTaskNode create(String id,DAG dag,boolean isKeyNode){
        return new DefaultTaskNode(id,dag,isKeyNode);
    }

    /**
     * the default constructor
     *
     * @param id  id
     * @param dag
     */
    protected DefaultTaskNode(String id, DAG dag,boolean isKeyNode) {
        super(id, dag, isKeyNode);
    }

    @Override
    public Class<DefaultTask> getBelong() {
        return DefaultTask.class;
    }

    @Override
    public String toString() {
        return "DefaultTaskNode{}";
    }

    @Override
    public DefaultEventNode copy() {
         throw new UnsupportedOperationException("task node can not copy");
    }
}
