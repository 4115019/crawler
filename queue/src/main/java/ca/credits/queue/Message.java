package ca.credits.queue;

import ca.credits.common.util.RandomUtil;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by chenwen on 16/9/20.
 */
public class Message implements Serializable{
    /**
     * serial version
     */
    private static final long serialVersionUID = 1L;

    @Getter
    private String _id = RandomUtil.getRandomId();
}
