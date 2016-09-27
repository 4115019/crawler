package ca.credits.business.util;

import ca.credits.common.config.Config;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import us.codecraft.webmagic.Template;

import java.util.Collection;

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
            getMapper().save(template, DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName)).build());
        }
    }
}
