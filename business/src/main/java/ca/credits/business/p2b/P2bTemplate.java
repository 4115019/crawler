package ca.credits.business.p2b;

import ca.credits.business.AbstractTemplate;
import ca.credits.business.enums.PlatformCodeEnum;

import java.util.Date;

/**
 * Created by chenwen on 16/9/21.
 */
public class P2bTemplate extends AbstractTemplate {
    /**
     * 唯一主键 , 哈希序列，唯一标识该条记录
     */
    private String primaryKey;

    /**
     * 姓名 , 刘真实
     */
    private String name;

    /**
     * 身份证号 , 43062319920706****
     */
    private String custId;

    /**
     * 手机号 , 18689861***
     */
    private String phone;

    /**
     * qq号 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String qqNum;

    /**
     * 邮箱 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String email;

    /**
     * 居住地址 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String address;

    /**
     * 学校 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String school;

    /**
     * 用户id , pdu7418145516
     */
    private String userId;

    /**
     * 用户昵称 , 无法获取该字段，但是依旧保留，取值为空
     */
    private String nickName;

    /**
     * 平台编码 , nameCode枚举类
     */
    private String platCode;

    /**
     * 不良原因编码 , BadReason枚举类
     */
    private String badFlag;

    /**
     * 发布时间 , yyyy-mm-dd hh:mm:ss, 无法获取该字段，但是依旧保留，取值为空
     */
    private Date publishTime;

    /**
     * 爬取时间 , yyyy-mm-dd hh:mm:ss
     */
    private Date crawlTime;

    /**
     * 删除日期 , yyyy-mm-dd hh:mm:ss
     */
    private Date jrjtDelDt;

    public P2bTemplate(PlatformCodeEnum.INameCode nameCode) {
        super(nameCode);
        this.platCode = nameCode.getCode();
        this.crawlTime = new Date();
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getQqNum() {
        return qqNum;
    }

    public void setQqNum(String qqNum) {
        this.qqNum = qqNum == null ? null : qqNum.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getPlatCode() {
        return platCode;
    }

    public void setPlatCode(String platCode) {
        this.platCode = platCode;
    }

    public String getBadFlag() {
        return badFlag;
    }

    public void setBadFlag(String badFlag) {
        this.badFlag = badFlag;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Date crawlTime) {
        this.crawlTime = crawlTime;
    }

    public Date getJrjtDelDt() {
        return jrjtDelDt;
    }

    public void setJrjtDelDt(Date jrjtDelDt) {
        this.jrjtDelDt = jrjtDelDt;
    }
}
