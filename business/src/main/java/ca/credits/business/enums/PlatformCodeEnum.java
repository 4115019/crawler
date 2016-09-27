package ca.credits.business.enums;

/**
 * Created by chenwen on 16/9/21.
 */
public enum PlatformCodeEnum {
    ;

    public interface INameCode {
        String getName();
        String getCode();
    }

    public enum BadReason implements INameCode{
        OVERDUE("恶意逾期","bad_rsn_1"),
        OTHER("其他","bad_rsn_2"),
        ;
        private final String name;

        private final String code;

        BadReason(String name,String code){
            this.name = name;
            this.code = code;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum P2B implements INameCode {
        PPDAI("拍拍贷","P00001"),
        WANGDAI("网贷研究院","P00002"),
        LEDAO("乐道投资","P00003"),
        XINDAI808("808信贷","P00004"),
        JIEDAIP2P("借贷P2P","P00005"),
        SHIXIN("老赖","P00006"),
        HAOMAOTONG("号码通","P00007"),
        ;
        private final String name;

        private final String code;

        P2B(String name,String code){
            this.name = name;
            this.code = code;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getCode() {
            return code;
        }
    }

    public enum VirtualPhone implements INameCode {
        ALIPAY("阿里小号","X00001"),
        XIAOHAO51("51小号","X00002"),
        EMA("E码平台","J00001"),
        SHENHUA("神话","J00002"),
        YMA("Y码平台","J00003"),
        ;
        private final String name;

        private final String code;

        VirtualPhone(String name,String code){
            this.name = name;
            this.code = code;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getCode() {
            return code;
        }
    }
}
