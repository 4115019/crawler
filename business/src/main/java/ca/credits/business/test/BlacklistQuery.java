package ca.credits.business.test;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.util.DynamoDBUtil;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangpin on 16/9/22.
 */
public class BlacklistQuery {
    public static void main(String[] args) throws Exception {
        /**
         * 第一个字符串是秘钥ID,第二个字符串是秘钥内容,秘钥的获取是从aws网站生成的.
         */
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials("AKIAPDS2XPCGM7YDOFNA", "cDWdKipWC1iHzYsbKds06Dparx9+MgQa+3Gbl8a+"));

        /**
         * 这个是aws中国地区的终端节点.
         */
        client.setEndpoint("dynamodb.cn-north-1.amazonaws.com.cn");

        /**
         * 在aws客户端建立表后,使用aws-java-sdk-dynamodb包提供的mapper对表格进行操作
         */
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        /**
         * 插
         */
        P2bTemplate testPojo = new P2bTemplate(PlatformCodeEnum.P2B.PPDAI);
        testPojo.setName("刘阿萨");
        testPojo.setCustId("43062319920706****");
        testPojo.setPhone("18689861***");
        testPojo.setQqNum("4761241233");
        testPojo.setEmail("4761241233@qq.com");
        testPojo.setAddress("北京");
        testPojo.setUserId("0001");
        testPojo.setNickName("无敌");
        testPojo.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());

        DynamoDBUtil.save(testPojo,"blacklist-test");

//        mapper.save(testPojo, DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride("blacklist-test")).build());


//        /**
//         * 查
//         */
//        P2bTemplate result = new P2bTemplate();
//        result.setId("123");
//        DynamoDBQueryExpression<TestPojo> queryExpression = new DynamoDBQueryExpression<TestPojo>()
//                .withHashKeyValues(result);
//        List<TestPojo> itemList = mapper.query(TestPojo.class, queryExpression);
//
//        for (int i = 0; i < itemList.size(); i++) {
//            System.out.println(itemList.get(i).getInfo());
//            System.out.println(itemList.get(i).getText());
//        }
    }
}
