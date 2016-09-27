package ca.credits.base.gateway;

import ca.credits.base.IExecutive;
import ca.credits.common.util.ListUtil;
import lombok.extern.slf4j.Slf4j;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chenwen on 16/8/26.ßß
 */
@Slf4j
public class DefaultGateway implements IGateway {
    /**
     * gateway pattern
     * eg: task1 & task2 || task3
     */
    private String gateway;

    /**
     * the default constructor
     * @param gateway gateway
     */
    public DefaultGateway(String gateway){
        this.gateway = gateway;
    }

    /**
     * this suggest
     * @param parents this is current event's parents
     * @return
     */
    @Override
    public GatewaySuggest suggest(List<IExecutive> parents) throws ParseException{
        /**
         * 没有父节点(要么是StartEvent,要么是当前正在执行的根Task)
         */
        if (ListUtil.isEmpty(parents)){
            return GatewaySuggest.UNDO;
        }

        if (gateway == null || gateway.length() == 0){
            return suggestAnd(parents);
        }

        String exprStr = gateway;
        /**
         * 是否父节点都执行完毕
         */
        for(IExecutive parent : parents){
            GatewaySuggest suggest = suggest(parent.getStatus());
            if (suggest == GatewaySuggest.UNDO){
                return GatewaySuggest.UNDO;
            }else {
                exprStr = exprStr.replace(parent.getId(),suggest == GatewaySuggest.NEXT ? "1" : "0");
            }
        }
        /**
         * 计算gateway表达式的值
         */
        try {
            Expression expr = Parser.parse(exprStr);
            return expr.evaluate() > 0.0 ? GatewaySuggest.NEXT : GatewaySuggest.EXCEPTION;
        } catch (ParseException e) {
            log.error("解析异常gateway ===> ",e);
            throw e;
        }
    }

    /**
     * this suggest with gateway
     * @param gateway gateway
     * @param parents parents
     * @return GatewaySuggest
     */
    public GatewaySuggest suggest(String gateway,List<IExecutive> parents){
        if (gateway == null || gateway.length() == 0){
            return suggestAnd(parents);
        }
        gateway = gateway.replace(" ","");

        if (gateway.startsWith("(") && gateway.endsWith(")") && !gateway.substring(1,gateway.length()-1).contains(")")){
            return suggest(gateway.substring(1,gateway.length()-1),parents);
        }
        if (gateway.contains("(") && gateway.contains(")")){
            final int first = gateway.lastIndexOf("(");
            final int last = gateway.substring(first).indexOf(")") + first;

            String one = gateway.substring(0,first);

            String two = gateway.substring(first + 1,last);

            String three = gateway.substring(last + 1);

            if ((one.length() == 0 || (!one.endsWith("||") && !one.endsWith("&&")))|| (three.length() == 0 || (!three.startsWith("||") && !three.startsWith("&&"))) ){
                if (one.length() == 0 || (!one.endsWith("||") && !one.endsWith("&&"))){
                    return three.startsWith("||") ? suggest(three.substring(2),parents).or(suggest(two,parents)) : suggest(three.substring(2),parents).and(suggest(two,parents));
                }else {
                    return one.endsWith("||") ? suggest(one.substring(0,one.length()-2),parents).or(suggest(two,parents)) : suggest(one.substring(0,one.length()-2),parents).and(suggest(two,parents));
                }
            }else if (one.endsWith("||") && three.startsWith("||")){
                return suggest(one.substring(0,one.length()-2),parents).or(suggest(two,parents)).or(suggest(three.substring(2),parents));
            }else if (one.endsWith("||") && three.startsWith("&&")){
                return suggest(one.substring(0,one.length()-2),parents).or(suggest(two,parents)).and(suggest(three.substring(2),parents));
            }else if (one.endsWith("&&") && three.startsWith("||")){
                return suggest(one.substring(0,one.length()-2),parents).and(suggest(two,parents)).or(suggest(three.substring(2),parents));
            }else {
                return suggest(one.substring(0,one.length()-2),parents).and(suggest(two,parents)).and(suggest(three.substring(2),parents));
            }
        }else {
            return suggestWhenGatewayIsOrder(gateway.replace(")","").replace("(",""),parents);
        }
    }

    /**
     * 当 gateway 不包含括号
     * @param gateway gateway
     * @param parents parents
     * @return GatewaySuggest
     */
    private GatewaySuggest suggestWhenGatewayIsOrder(String gateway,List<IExecutive> parents){
        if (!gateway.contains("||") && !gateway.contains("&&")){
            return suggestAnd(getParents(new String[]{gateway},parents));
        }else if (gateway.contains("||") && !gateway.contains("&&")){
            return suggestOr(getParents(gateway.split("\\|\\|"),parents));
        }else if (gateway.contains("&&") && !gateway.contains("||")){
            return suggestAnd(getParents(gateway.split("&&"),parents));
        }else {
            int index = gateway.indexOf("&&");
            return suggestWhenGatewayIsOrder(gateway.substring(0,index),parents).and(suggestWhenGatewayIsOrder(gateway.substring(index + 2),parents));
        }
    }

    /**
     * suggest and
     * @param parents parents
     * @return GatewaySuggest
     */
    private GatewaySuggest suggestAnd(List<IExecutive> parents){
        GatewaySuggest suggest = suggest(parents.get(0).getStatus());
        for(int i = 1 ; i < parents.size(); ++i){
           suggest = suggest.and(suggest(parents.get(i).getStatus()));
        }
        return suggest;
    }

    /**
     * suggest or
     * @param parents parents
     * @return GatewaySuggest
     */
    private GatewaySuggest suggestOr(List<IExecutive> parents){
        GatewaySuggest suggest = suggest(parents.get(0).getStatus());
        for(int i = 1 ; i < parents.size(); ++i){
            suggest = suggest.or(suggest(parents.get(i).getStatus()));
        }
        return suggest;
    }

    /**
     * suggest by status
     * @param status status
     * @return GatewaySuggest
     */
    private GatewaySuggest suggest(IExecutive.Status status){
        switch (status){
            case UNDO:
            case RUNNING:
                return GatewaySuggest.UNDO;
            case EXCEPTION:
                return GatewaySuggest.EXCEPTION;
        }
        return GatewaySuggest.NEXT;
    }

    /**
     * get IExecutive
     * @param executives executives
     * @param parents parents
     * @return IExecutive
     */
    private List<IExecutive> getParents(String[] executives,List<IExecutive> parents){
        List<IExecutive> result = new ArrayList<>();
        for(String executive : executives){
            result.addAll(parents.parallelStream().filter(parent -> parent.getId().equals(executive)).collect(Collectors.toList()));
        }
        return result;
    }

}
