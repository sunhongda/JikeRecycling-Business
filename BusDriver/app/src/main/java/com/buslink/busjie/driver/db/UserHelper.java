package com.buslink.busjie.driver.db;

import com.buslink.busjie.driver.manager.DbManager;
import com.buslink.busjie.driver.manager.MyApplication;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

/**
 * Created by yanlong.luo on 2015/9/7.
 */
public class UserHelper {

    private static UserHelper instance;
    private User mUser;

    public static UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        instance.getUser();
        return instance;
    }

    private User getUser() {
        if (mUser == null) {
            mUser = getDbUser();
        }
        if (mUser == null) {
            mUser = new User();
        }
        return mUser;
    }

    private User getDbUser() {
        User user = null;
        DbUtils db = DbUtils.create(MyApplication.getContext());
        try {
            List<User> list = db.findAll(User.class);
            if (list != null && list.size() > 0) {
                user = list.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void setBdappid(String appid) {
        mUser.setBdappid(appid);
        updateUser();
    }

    public String getBdappid() {
        return mUser.getBdappid();
    }

    public void setBduserid(String userId) {
        mUser.setBduserid(userId);
        updateUser();
    }

    public String getBduserid() {
        return mUser.getBduserid();
    }

    public void setBdchannelid(String channelId) {
        mUser.setBdchannelid(channelId);
        updateUser();
    }

    public String getBdchannelid() {
        return mUser.getBdchannelid();
    }

    public void setUid(String uid) {
        mUser.setUid(uid);
        updateUser();
    }

    public String getUid() {
        return mUser.getUid();
    }

    public void setCid(String cid) {
        mUser.setCid(cid);
        updateUser();
    }

    public String getCid() {
        return mUser.getCid();
    }

    public void setPhone(String phone) {
        mUser.setMobile(phone);
        updateUser();
    }

    public String getPhone() {
        return mUser.getMobile();
    }

    public void setName(String name) {
        mUser.setName(name);
        updateUser();
    }

    public String getName() {
        return mUser.getName();
    }

    public void setSex(String sex) {
        mUser.setSex(sex);
        updateUser();
    }

    public String getSex() {
        return mUser.getSex();
    }

    public void setCompany(String company) {
        mUser.setCompany(company);
        updateUser();
    }

    public String getCompany() {
        return mUser.getCompany();
    }

    public void setBackupPhone(String backupPhone) {
        mUser.setBackupmobile(backupPhone);
        updateUser();
    }

    public String getBackupPhone() {
        return mUser.getBackupmobile();
    }

    public void setBusNumber(String busNumber) {
        mUser.setBusnumber(busNumber);
        updateUser();
    }

    public String getBusNumber() {
        return mUser.getBusnumber();
    }

    public void setBusCity(String busCity) {
        mUser.setBuscity(busCity);
        updateUser();
    }

    public String getBusCity() {
        return mUser.getBuscity();
    }

    public void setBusBrand(String busBrand) {
        mUser.setBusbrand(busBrand);
        updateUser();
    }

    public String getBusBrand() {
        return mUser.getBusbrand();
    }

    public void setSeat(String seat) {
        mUser.setSeat(seat);
        updateUser();
    }

    public String getSeat() {
        return mUser.getSeat();
    }

    public void setBusType(String busType) {
        mUser.setBustype(busType);
        updateUser();
    }

    public String getBusType() {
        return mUser.getBustype();
    }

    public void setHeadUrl(String headUrl) {
        mUser.setHeadurl(headUrl);
        updateUser();
    }

    public String getHeadUrl() {
        return mUser.getHeadurl();
    }

    public void setLicenceUrl(String licenceUrl) {
        mUser.setLicenceurl(licenceUrl);
        updateUser();
    }

    public String getLicenceUrl() {
        return mUser.getLicenceurl();
    }

    public void setJobcardUrl(String jobcardUrl) {
        mUser.setJobcardurl(jobcardUrl);
        updateUser();
    }

    public String getJobcardUrl() {
        return mUser.getJobcardurl();
    }

    public void setBusUrl(String busUrl) {
        mUser.setBusurl(busUrl);
        updateUser();
    }

    public String getBusUrl() {
        return mUser.getBusurl();
    }

    public void setInsuranceUrl(String insuranceUrl) {
        mUser.setInsuranceurl(insuranceUrl);
        updateUser();
    }

    public String getInsuranceUrl() {
        return mUser.getInsuranceurl();
    }

    public void setRoadUrl(String roadUrl) {
        mUser.setRoadurl(roadUrl);
        updateUser();
    }

    public String getRoadUrl() {
        return mUser.getRoadurl();
    }

    public void setPid(String pid) {
        mUser.setPid(pid);
        updateUser();
    }

    public String getPid() {
        return mUser.getPid();
    }

    public void setBid(String bid) {
        mUser.setBid(bid);
        updateUser();
    }

    public String getBid() {
        return mUser.getBid();
    }

    private void updateUser() {
        try {
            DbManager.getDb().saveOrUpdate(mUser);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
