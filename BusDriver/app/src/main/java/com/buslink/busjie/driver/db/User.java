package com.buslink.busjie.driver.db;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by yanlong.luo on 2015/9/5.
 */
@Table(name = "Driver")
public class User extends EntityBase{

    @Column(column = "test")
    public String test;

    @Column(column = "uid")
    public String uid; // 司机id

    @Column(column = "cid")
    public String cid; // 车辆id

    @Column(column = "pid")
    public String pid; // 零钱id

    @Column(column = "bid")
    public String bid; // 银行卡id

    @Column(column = "mobile")
    public String mobile; // 手机

    @Column(column = "backupmobile")
    public String backupmobile; // 备用电话

    @Column(column = "bdappid")
    public String bdappid;

    @Column(column = "bduserid")
    public String bduserid;

    @Column(column = "bdchannelid")
    public String bdchannelid;

    @Column(column = "headurl")
    public String headurl; // 司机头像

    @Column(column = "licenceurl")
    public String licenceurl; // 驾驶证

    @Column(column = "jobcardurl")
    public String jobcardurl; //  从业资格证

    @Column(column = "busurl")
    public String busurl; // 汽车头像

    @Column(column = "insuranceurl")
    public String insuranceurl; // 保险

    @Column(column = "roadurl")
    public String roadurl; //  行驶证

    @Column(column = "name")
    public String name; // 司机姓名

    @Column(column = "sex")
    public String sex; // 性别

    @Column(column = "company")
    public String company; // 公司

    @Column(column = "busnumber")
    public String busnumber; // 车牌号

    @Column(column = "buscity")
    public String buscity; // 车辆地区

    @Column(column = "busbrand")
    public String busbrand; // 车辆品牌

    @Column(column = "seat")
    public String seat; // 座位数

    @Column(column = "bustype")
    public String bustype; // 车辆类型

    @Column(column = "star")
    public String star; // 星级

    @Column(column = "ordersum")
    public String ordersum; // 接单总数

    @Column(column = "rank")
    public String rank; // 排名

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBackupmobile() {
        return backupmobile;
    }

    public void setBackupmobile(String backupmobile) {
        this.backupmobile = backupmobile;
    }

    public String getBdappid() {
        return bdappid;
    }

    public void setBdappid(String bdappid) {
        this.bdappid = bdappid;
    }

    public String getBduserid() {
        return bduserid;
    }

    public void setBduserid(String bduserid) {
        this.bduserid = bduserid;
    }

    public String getBdchannelid() {
        return bdchannelid;
    }

    public void setBdchannelid(String bdchannelid) {
        this.bdchannelid = bdchannelid;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getLicenceurl() {
        return licenceurl;
    }

    public void setLicenceurl(String licenceurl) {
        this.licenceurl = licenceurl;
    }

    public String getJobcardurl() {
        return jobcardurl;
    }

    public void setJobcardurl(String jobcardurl) {
        this.jobcardurl = jobcardurl;
    }

    public String getBusurl() {
        return busurl;
    }

    public void setBusurl(String busurl) {
        this.busurl = busurl;
    }

    public String getInsuranceurl() {
        return insuranceurl;
    }

    public void setInsuranceurl(String insuranceurl) {
        this.insuranceurl = insuranceurl;
    }

    public String getRoadurl() {
        return roadurl;
    }

    public void setRoadurl(String roadurl) {
        this.roadurl = roadurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getBustype() {
        return bustype;
    }

    public void setBustype(String bustype) {
        this.bustype = bustype;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getOrdersum() {
        return ordersum;
    }

    public void setOrdersum(String ordersum) {
        this.ordersum = ordersum;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getBusnumber() {
        return busnumber;
    }

    public void setBusnumber(String busnumber) {
        this.busnumber = busnumber;
    }

    public String getBuscity() {
        return buscity;
    }

    public void setBuscity(String buscity) {
        this.buscity = buscity;
    }

    public String getBusbrand() {
        return busbrand;
    }

    public void setBusbrand(String busbrand) {
        this.busbrand = busbrand;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}
