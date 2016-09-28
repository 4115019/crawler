package ca.credits.business.util;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.xiaohao.XiaohaoTemplate;
import ca.credits.common.config.Config;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import us.codecraft.webmagic.Template;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangpin on 16/9/26.
 */
public class DynamoDBUtil {
    private static DynamoDBMapper getMapper(){
        /**
         * 第一个字符串是秘钥ID,第二个字符串是秘钥内容,秘钥的获取是从aws网站生成的.
         */
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials(Config.getString("aws.dynamodb.key.name"), Config.getString("aws.dynamodb.key.value")));

        /**
         * 这个是aws中国地区的终端节点.
         */
        client.setEndpoint(Config.getString("aws.dynamodb.endpoint"));

        /**
         * 在aws客户端建立表后,使用aws-java-sdk-dynamodb包提供的mapper对表格进行操作
         */
        return new DynamoDBMapper(client);
    }

    public static void save(Template template,String tableName){
//        template.setPrimaryKey(template.getPrimaryKey());
        getMapper().save(template, DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName)).build());
    }

    public static <T extends Template> void save(Collection<T> templates,String tableName){
        for (Template template: templates){
            save(template,tableName);
        }
    }

    public static void main(String[] args) {
        /**
         * 查询表中总共多少条数据
         */
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();
        int count = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("count="+count);

        /**
         * 查询特定条件的数据总数
         */
        Map<String, Condition> filter = new HashMap<String, Condition>();
        filter.put("plat_code",new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS("X00001")));
        dynamoDBScanExpression.setScanFilter(filter);
        int x00001 = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("X00001="+x00001);

        filter.clear();
        filter.put("plat_code",new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS("X00002")));
        dynamoDBScanExpression.setScanFilter(filter);
        int x00002 = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("X00002="+x00002);

        filter.clear();
        filter.put("plat_code",new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS("J00001")));
        dynamoDBScanExpression.setScanFilter(filter);
        int j00001 = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("J00001="+j00001);

        filter.clear();
        filter.put("plat_code",new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS("J00002")));
        dynamoDBScanExpression.setScanFilter(filter);
        int j00002 = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("J00002="+j00002);

        filter.clear();
        filter.put("plat_code",new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(
                new AttributeValue().withS("J00003")));
        dynamoDBScanExpression.setScanFilter(filter);
        int j00003 = getMapper().count(XiaohaoTemplate.class,dynamoDBScanExpression);
        System.out.println("J00003="+j00003);

        System.out.println(j00001 + j00002 + j00003 + x00001 + x00002);
    }
}
