package ca.credits.base;

import ca.credits.base.event.AbstractEvent;
import ca.credits.base.gateway.DefaultGateway;
import ca.credits.base.gateway.IGateway;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwen on 16/8/26.
 */
@Slf4j
public class DefaultGatewayTest {
    @Test
    public void testSuggest() throws ParseException {

        Executive one = new Executive("one", IExecutive.Status.DONE);

        Executive two = new Executive("two", IExecutive.Status.EXCEPTION);

        Executive three = new Executive("three", IExecutive.Status.DONE);

        Executive four = new Executive("four", IExecutive.Status.EXCEPTION);


        final List<IExecutive> parents = new ArrayList<IExecutive>(){{
            add(one);
            add(two);
            add(three);
            add(four);
        }};

        IGateway gateway = new DefaultGateway("one || two || three || four");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.NEXT);

        gateway = new DefaultGateway("one && two || three || four");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.NEXT);

        gateway = new DefaultGateway("one && two && three || four");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("one && two && three && four");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("(one && two) && (three && four)");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("((((one && two) && (three && four))))");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.EXCEPTION);

        gateway = new DefaultGateway("one || ((two) && (three && four))");
        Assert.assertEquals(gateway.suggest(parents), IGateway.GatewaySuggest.NEXT);
    }


    @Test
    public void testParsii() throws ParseException {
        Expression expression = Parser.parse("1||(0&&0)");

        log.info(String.valueOf(expression.evaluate()));
    }

    class Executive extends AbstractEvent {
        public Executive(String id,Status status){
            super("loggerEngine","test",null,null);
            this.id = id;
            this.status.set(status.ordinal());
        }
    }
}
