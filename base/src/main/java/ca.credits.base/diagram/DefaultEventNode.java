package ca.credits.base.diagram;

import ca.credits.base.event.DefaultEvent;
import ca.credits.common.Properties;

/**
 * Created by chenwen on 16/9/1.
 */
public class DefaultEventNode extends AbstractEventNode {
    /**
     * the creator
     * @param id id
     * @return
     */
    public static DefaultEventNode create(String id,String workerClass){
        return new DefaultEventNode(id,workerClass,false);
    }


    public static DefaultEventNode create(String id,String workerClass,boolean isKeyNode){
        return new DefaultEventNode(id,workerClass,isKeyNode);
    }

    /**
     * default constructor
     * @param id id
     */
    protected DefaultEventNode(String id,String workerClass,boolean isKeyNode) {
        super(id,workerClass,isKeyNode);
    }

    @Override
    public DefaultEventNode copy(){
        DefaultEventNode node = DefaultEventNode.create(this.getId(),this.workerClass,this.isKeyNode());
        node.timeout(this.getTimeout());
        Properties clone = (Properties) this.getProperties().clone();
        node.setProperties(clone);
        return node;
    }

    @Override
    public Class<DefaultEvent> getBelong() {
        return DefaultEvent.class;
    }

    @Override
    public String toString() {
        return "DefaultEventNode{}";
    }
}
