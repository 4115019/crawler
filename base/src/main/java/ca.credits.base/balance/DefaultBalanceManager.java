package ca.credits.base.balance;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by chenwen on 16/9/6.
 */
@Named
@Singleton
public class DefaultBalanceManager implements IBalanceManager {
    @Override
    public void manage(IBalance balance) {

    }
}
