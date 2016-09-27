package ca.credits.base.diagram;

import lombok.Builder;
import lombok.Data;

/**
 * Created by chenwen on 16/9/1.
 * this class is the edge of event stream
 */
@Data
@Builder
public class Edge {
    /**
     * the edge id
     */
    private String id;

    /**
     * the source node
     */
    private AbstractNode source;

    /**
     * the target node
     */
    private AbstractNode target;
}
