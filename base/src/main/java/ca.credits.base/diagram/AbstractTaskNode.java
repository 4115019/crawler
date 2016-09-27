package ca.credits.base.diagram;

import lombok.Getter;

/**
 * Created by chenwen on 16/9/1.
 */
public abstract class AbstractTaskNode extends AbstractNode{
    /**
     * every task node have a dag
     */
    @Getter
    private DAG dag;

    /**
     * the default constructor
     * @param id id
     */
    protected AbstractTaskNode(String id,DAG dag) {
        this(id,dag,false);
    }

    protected AbstractTaskNode(String id,DAG dag,boolean isKeyNode) {
        super(id,isKeyNode);
        this.dag = dag;
    }

    @Override
    public String show(){
        return dag.show();
    }
}
