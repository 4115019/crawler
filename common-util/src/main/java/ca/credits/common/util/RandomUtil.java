package ca.credits.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by cw on 15-12-16.
 */
public class RandomUtil {
    public final static String format_yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public static String getRandomId(){
        String str = UUID.randomUUID().toString().toUpperCase().replace("-","") + UUID.randomUUID().toString().toUpperCase().replace("-","");
        if (str.length() > 64){
            str = str.substring(0,64);
        }
        return str;
    }

    public static int getRandom(int length){
        return (int)(Math.random()*Math.pow(10,length)) + 1;
    }
    /**
     * get format date
     * @param format
     * @return
     */
    public static String getCurrentTimeAsStringByFormat(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }
}
