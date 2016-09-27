package ca.credits.business;

import ca.credits.business.enums.PlatformCodeEnum;
import lombok.Getter;
import lombok.Setter;
import us.codecraft.webmagic.Template;

/**
 * Created by chenwen on 16/9/21.
 */
public abstract class AbstractTemplate implements Template {
    @Getter
    private PlatformCodeEnum.INameCode nameCode;

    public AbstractTemplate(PlatformCodeEnum.INameCode nameCode){
        this.nameCode = nameCode;
    }
}
