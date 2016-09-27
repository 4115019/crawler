package ca.credits.base;

import ca.credits.base.diagram.AbstractNode;
import ca.credits.base.diagram.AbstractTaskNode;
import ca.credits.base.task.AbstractTask;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by chenwen on 16/9/2.
 * this is a Sample Factory
 */
@Slf4j
public class ExecutiveFactory {

    /**
     * create executive product
     * @param node node
     * @param activityId activityId
     * @param regulator regulator
     * @return
     */
    public static IExecutive createExecutive(AbstractNode node,String activityId,IExecutiveManager regulator)throws InvocationException{
        IExecutive result = null;
        if (node instanceof AbstractTaskNode) {
            Constructor<?>[] constructors = node.getBelong().getConstructors();
            for(Constructor constructor : constructors) {
                try {
                    result = (IExecutive) constructor.newInstance(activityId, node, regulator);
                    break;
                }catch (IllegalAccessException | InstantiationException | InvocationTargetException e){
                    throw new InvocationException("create executive instance failed, the constructor should like this. constructor(String activityId,AbstractNode node,IExecutiveManager regulator)",e);
                }
            }
        }else {
            Constructor<?>[] constructors = node.getBelong().getConstructors();
            for(Constructor constructor : constructors) {
                try {
                    result = (IExecutive) constructor.newInstance(node, regulator);
                    break;
                }catch (IllegalAccessException | InstantiationException | InvocationTargetException e){
                    throw new InvocationException("create executive instance failed, the constructor should like this. constructor(AbstractNode node,IExecutiveManager regulator)",e);
                }
            }
        }
        return result;
    }
}
