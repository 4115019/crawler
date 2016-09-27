package ca.credits.engine.parser;

import ca.credits.engine.AbstractResponse;

/**
 * Created by chenwen on 16/9/18.
 */
public interface IResponseProcessor {
    /**
     * 处理下载结果
     * @param response 下载结果
     */
    void process(AbstractResponse response);
}
