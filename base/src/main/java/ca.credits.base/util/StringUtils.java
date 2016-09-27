package ca.credits.base.util;

import ca.credits.base.IExecutive;

/**
 * Created by chenwen on 16/9/6.
 */
public class StringUtils {
    /**
     * format executive when executive exception
     * @param executive executive
     * @param message message
     * @param args error args
     * @return executive error string
     */
    public static String formatExecutive(IExecutive executive,Object args ,String message){
        return String.format("%s message = %s args = %s",formatExecutive(executive),message,args.toString());
    }

    /**
     * format executive when executive exception
     * @param executive executive
     * @param message message
     * @return executive error string
     */
    public static String formatExecutive(IExecutive executive,String message){
        return String.format("%s message = %s",formatExecutive(executive),message);
    }

    /**
     * format executive id
     * @param executive executive
     * @return executive id
     */
    public static String formatExecutive(IExecutive executive){
        return String.format("activity = %s and taskId = %s and id = %s",executive.getActivityId(),executive.getTaskId(),executive.getId());
    }
}
