package ca.credits.base.gateway;

import ca.credits.base.IExecutive;
import parsii.tokenizer.ParseException;

import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
public interface IGateway {

    /**
     * suggest next event
     * @param parents this is current event's parents
     * @return
     */
    GatewaySuggest suggest(List<IExecutive> parents) throws ParseException;

    /**
     * gateway suggest
     */
    enum GatewaySuggest{
        /**
         * undo
         */
        UNDO,
        /**
         * next
         */
        NEXT,
        /**
         * exception
         */
        EXCEPTION;

        /**
         * this || gatewaySuggest
         * @param gatewaySuggest gatewaySuggest
         * @return GatewaySuggest
         */
        public GatewaySuggest or(GatewaySuggest gatewaySuggest){
            return this == GatewaySuggest.NEXT || gatewaySuggest == GatewaySuggest.NEXT
                    ? GatewaySuggest.NEXT : this == GatewaySuggest.EXCEPTION && gatewaySuggest == GatewaySuggest.EXCEPTION
                    ? GatewaySuggest.EXCEPTION : GatewaySuggest.UNDO;
        }

        /**
         * this && gatewaySuggest
         * @param gatewaySuggest gatewaySuggest
         * @return GatewaySuggest
         */
        public GatewaySuggest and(GatewaySuggest gatewaySuggest){
            return this == GatewaySuggest.NEXT && gatewaySuggest == GatewaySuggest.NEXT
                    ? GatewaySuggest.NEXT : this == GatewaySuggest.EXCEPTION || gatewaySuggest == GatewaySuggest.EXCEPTION
                    ? GatewaySuggest.EXCEPTION : GatewaySuggest.UNDO;
        }
    }
}
