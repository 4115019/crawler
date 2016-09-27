package ca.credits.engine;

import ca.credits.base.diagram.AbstractEventNode;
import ca.credits.base.kit.Constants;
import ca.credits.common.Properties;
import ca.credits.engine.kit.UrlUtils;
import ca.credits.engine.parser.selector.Html;
import ca.credits.engine.parser.selector.Json;
import ca.credits.engine.parser.selector.PlainText;
import ca.credits.engine.parser.selector.Selectable;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Created by chenwen on 16/8/19.
 */
public abstract class AbstractResponse {
    @Getter
    private AbstractRequest request;

    @Getter
    private String rawText;

    @Getter
    private Selectable url;

    @Getter
    private int statusCode;

    @Getter
    private Properties properties;

    private Collection<AbstractEventNode> nodes;

    private Html html;

    private Json json;


    protected AbstractResponse(AbstractRequest request, int statusCode, String rawText){
        this.request = request;
        this.url = new PlainText(request.getUrl());
        this.statusCode = statusCode;
        this.rawText = rawText;
        this.properties = new Properties();
        this.nodes = new Vector<>();
        this.properties.put(Constants.DYNAMIC_NODES,this.nodes);
    }

    /**
     * add next url
     * @param url next url
     */
    public void addTargetUrl(String url){
        if (StringUtils.isBlank(url) || url.equals("#")) {
            return;
        }

        url = UrlUtils.canonicalizeUrl(url, this.url.toString());
        AbstractEventNode node = request.getEvent().getNode().copy();
        node.property(Constants.RequestParams.URL,url);
        node.property(Constants.RequestParams.Headers.REFERER,this.url.toString());
        node.setId(request.getEvent().getRandomChildId());

        this.addTargetEventNode(node);
    }

    /**
     * add target urls
     * @param urls next urls
     */
    public void addTargetRequestUrls(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)){
            return;
        }
        urls.forEach(this::addTargetUrl);
    }

    /**
     * add target event node
     * @param node
     */
    public void addTargetEventNode(AbstractEventNode node){
        if (node != null){
            this.nodes.add(node);
        }
    }

    /**
     * add target event node
     * @param nodes
     */
    public void addTargetEventNodes(Collection<AbstractEventNode> nodes){
        if (nodes != null){
            this.nodes.addAll(nodes);
        }
    }

    /**
     * get html response
     * @return html
     */
    public Html getHtml(){
        if (html == null) {
            html = new Html(UrlUtils.fixAllRelativeHrefs(rawText, request.getUrl()));
        }
        return html;
    }

    /**
     * get json response
     * @return json
     */
    public Json getJson() {
        if (json == null) {
            json = new Json(rawText);
        }
        return json;
    }
}
