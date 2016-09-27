package ca.credits.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by chenwen on 16/8/29.
 */
public class BeansFactory {
    private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:*-beans.xml");

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(String name)throws BeansException{
        return (T) context.getBean(name);
    }
//    public static <T> T getBean(Class<T> tClass) throws BeansException{
//        return context.getBean(tClass);
//    }
}
