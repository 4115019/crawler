package ca.credits.base.diagram;

import ca.credits.base.ComponentFactory;
import ca.credits.common.util.ListUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Created by chenwen on 16/9/1.
 * this class is the diagram of event stream
 *
 * DAG 有向无环图
 */
@Slf4j
public class DAG {
    /**
     * start abstractNode
     */
    private AbstractNode startAbstractNode;

    /**
     * end abstractNode
     */
    private AbstractNode endAbstractNode;

    /**
     * the key is abstractNode id , the value is abstractNode
     */
    @Getter
    private Map<String,AbstractNode> nodeMap = new HashMap<>();

    /**
     * the key is edge id , the value is edge
     */
    @Getter
    private Map<String,Edge> edgeMap = new HashMap<>();

    /**
     * the lockTimeUnit
     */
    protected TimeUnit timeUnit = TimeUnit.MILLISECONDS;

    /**
     * the get lock lockTime
     */
    protected long time = 60000;

    /**
     * get lock
     */
    protected Lock lock = ComponentFactory.getLock(getLockKey());

    /**
     * default create method
     * @return DAG
     */
    public static DAG create(){
        return new DAG();
    }

    /**
     * default constructor
     */
    private DAG(){
    }

    /**
     * 设置开始node
     * @param abstractNode end abstractNode
     * @return
     */
    public DAG withStartNode(AbstractNode abstractNode) throws InterruptedException {
        this.startAbstractNode = abstractNode;
        return addNodes(abstractNode);
    }

    /**
     * 设置结束node
     * @param abstractNode start abstractNode
     * @return
     */
    public DAG withEndNode(AbstractNode abstractNode) throws InterruptedException {
        this.endAbstractNode = abstractNode;
        return addNodes(abstractNode);
    }

    /**
     * add abstractNodes to diagram
     * @param abstractNodes abstractNodes
     * @return
     */
    private DAG addNodes(List<AbstractNode> abstractNodes) throws InterruptedException {
        try{
            if (lock.tryLock(time,timeUnit)){
                abstractNodes.stream().forEach(node -> {
                    if (!nodeMap.containsKey(node.getId())){
                        nodeMap.put(node.getId(),node);
                    }
                });
            }
        }finally {
            if (lock != null){
                lock.unlock();
            }
        }
        return this;
    }
    /**
     * add abstractNodes to diagram
     * @param abstractNodes abstractNodes
     * @return
     */
    private DAG addNodes(AbstractNode... abstractNodes) throws InterruptedException {
        return addNodes(Arrays.asList(abstractNodes));
    }
    /**
     * add abstractNodes to diagram
     * @param edges edges
     * @return
     */
    public synchronized DAG addEdges(List<Edge> edges) throws InterruptedException {
        try{
            if (lock.tryLock(time,timeUnit)){
                edges.stream().forEach(edge -> {
                    if (!edgeMap.containsKey(edge.getId())){
                        edgeMap.put(edge.getId(),edge);
                        try {
                            addNodes(edge.getSource(),edge.getTarget());
                            edge.getSource().addChildren(edge.getTarget());
                            edge.getTarget().addParents(edge.getSource());
                        } catch (InterruptedException e) {
                            log.error("lock exception",e);
                        }
                    }
                });
            }
        }finally {
            if (lock != null){
                lock.unlock();
            }
        }
        return this;
    }

    /**
     * add edges to diagram
     * @param edges edges
     * @return
     */
    public DAG addEdges(Edge... edges) throws InterruptedException {
        return addEdges(Arrays.asList(edges));
    }

    /**
     * show diagram
     */
    public String show(){
        return getStartAbstractNode().show();
    }

    /**
     * get start abstractNode
     * @return start abstractNode
     */
    public AbstractNode getStartAbstractNode() {
        if (startAbstractNode == null){
            for(AbstractNode abstractNode : nodeMap.values()){
                if (ListUtil.isEmpty(abstractNode.getParents())){
                    abstractNode.keyNode(true);
                    return startAbstractNode = abstractNode;
                }
            }
        }
        return startAbstractNode;
    }

    /**
     * get end abstractNode
     * @return end abstractNode
     */
    public AbstractNode getEndAbstractNode() {
        if (endAbstractNode == null){
            for(AbstractNode abstractNode : nodeMap.values()){
                if (ListUtil.isEmpty(abstractNode.getChildren())){
                    abstractNode.keyNode(true);
                    return endAbstractNode = abstractNode;
                }
            }
        }
        return endAbstractNode;
    }

    /**
     * lock id
     * @return lock
     */
    protected String getLockKey(){
        return String.format("%s_%s",this.getClass().getName(), UUID.randomUUID().toString());
    }
}
