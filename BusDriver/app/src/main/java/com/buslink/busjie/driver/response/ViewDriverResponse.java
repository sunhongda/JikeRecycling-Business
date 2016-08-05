package com.buslink.busjie.driver.response;

import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/26.
 */
public class ViewDriverResponse extends ParseJSON{

    private String cardimg;
    private String company;
    private String dimg;
    private String driverphoto;
    private int gender;
    private String nickname;
    private String phone;
    private String reservephone;
    private int veritystate;
    private String workphoto;

    @Override
    public void parse(String jsonString) {
        resultString = jsonString;
        JSONObject jo = XString.getJSONObject(jsonString);
        JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
        status = XString.getBoolean(jo, JsonName.STATUS);
        if (!status) {
            msg = XString.getStr(data, JsonName.MSG);
            return;
        }
        cardimg = XString.getStr(data, "cardimg");
        company = XString.getStr(data, "company");
        dimg = XString.getStr(data, "dimg");
        driverphoto = XString.getStr(data, "driverphoto");
        gender = XString.getInt(data, "gender");
        nickname = XString.getStr(data, "nickname");
        phone = XString.getStr(data, "phone");
        reservephone = XString.getStr(data, "reservephone");
        veritystate = XString.getInt(data, "veritystate");
        workphoto = XString.getStr(data, "workphoto");
    }

    public void setCardimg(String cardimg) {
        this.cardimg = cardimg;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setDimg(String dimg) {
        this.dimg = dimg;
    }

    public void setDriverphoto(String driverphoto) {
        this.driverphoto = driverphoto;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setReservephone(String reservephone) {
        this.reservephone = reservephone;
    }

    public void setVeritystate(int veritystate) {
        this.veritystate = veritystate;
    }

    public void setWorkphoto(String workphoto) {
        this.workphoto = workphoto;
    }

    public String getCardimg() {
        return cardimg;
    }

    public String getCompany() {
        return company;
    }

    public String getDimg() {
        return dimg;
    }

    public String getDriverphoto() {
        return driverphoto;
    }

    public int getGender() {
        return gender;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPhone() {
        return phone;
    }

    public String getReservephone() {
        return reservephone;
    }

    public int getVeritystate() {
        return veritystate;
    }

    public String getWorkphoto() {
        return workphoto;
    }
}
