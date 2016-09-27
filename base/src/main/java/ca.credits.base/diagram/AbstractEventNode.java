package ca.credits.base.diagram;

import lombok.Getter;

/**
 * Created by chenwen on 16/9/1.
 */
public abstract class AbstractEventNode extends AbstractNode{
    /**
     * the event worker class
     */
    @Getter
    protected String workerClass;

    /**
     * default constructor
     * @param id id
     */
    protected AbstractEventNode(String id,String workerClass,boolean isKeyNode) {
        super(id,isKeyNode);
        this.workerClass = workerClass;
    }
}
