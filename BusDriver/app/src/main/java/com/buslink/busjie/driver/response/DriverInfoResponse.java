package com.buslink.busjie.driver.response;

import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/16.
 * 接口11 查询司机注册信息
 */
public class DriverInfoResponse extends ParseJSON{

    private String brand;
    private String cardimg; // 身份证
    private String carimg;
    private String carnumber;
    private int cartype; // 1普通型 2 经济型 3豪华型
    private String ccause;
    private String cid;
    private String city;
    private String company;
    private int cveritystate;
    private String dcause;
    private String did;
    private String dimg; // 司机头像
    private String drivercause;
    private String driverphoto; // 驾驶证
    private int dveritystate;
    private int gender; // 1男 2女
    private String insuranceimg; // 保险证
    private String nickname;
    private String phone;
    private String reservephone;
    private String roadphoto; // 行驶证
    private int seat;
    private String transportcertificate; // 经营许可证
    private String workphoto; // 从业资格证

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
        brand = XString.getStr(data, "brand");
        cardimg = XString.getStr(data, "cardimg");
        carimg = XString.getStr(data, "carimg");
        carnumber = XString.getStr(data, "carnumber");
        cartype = XString.getInt(data, "cartype");
        ccause = XString.getStr(data, "ccause");
        cid = XString.getStr(data, "cid");
        city = XString.getStr(data, "city");
        company = XString.getStr(data, "company");
        cveritystate = XString.getInt(data, "cveritystate");
        dcause = XString.getStr(data, "dcause");
        did = XString.getStr(data, "did");
        dimg = XString.getStr(data, "dimg");
        drivercause = XString.getStr(data, "drivercause");
        driverphoto = XString.getStr(data, "driverphoto");
        dveritystate = XString.getInt(data, "dveritystate");
        gender = XString.getInt(data, "gender");
        insuranceimg = XString.getStr(data, "insuranceimg");
        nickname = XString.getStr(data, "nickname");
        phone = XString.getStr(data, "phone");
        reservephone = XString.getStr(data, "reservephone");
        roadphoto = XString.getStr(data, "roadphoto");
        seat = XString.getInt(data, "seat");
        transportcertificate = XString.getStr(data, "transportcertificate");
        workphoto = XString.getStr(data, "workphoto");
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCardimg() {
        return cardimg;
    }

    public void setCardimg(String cardimg) {
        this.cardimg = cardimg;
    }

    public String getCarimg() {
        return carimg;
    }

    public void setCarimg(String carimg) {
        this.carimg = carimg;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public int getCartype() {
        return cartype;
    }

    public void setCartype(int cartype) {
        this.cartype = cartype;
    }

    public String getCcause() {
        return ccause;
    }

    public void setCcause(String ccause) {
        this.ccause = ccause;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getCveritystate() {
        return cveritystate;
    }

    public void setCveritystate(int cveritystate) {
        this.cveritystate = cveritystate;
    }

    public String getDcause() {
        return dcause;
    }

    public void setDcause(String dcause) {
        this.dcause = dcause;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getDimg() {
        return dimg;
    }

    public void setDimg(String dimg) {
        this.dimg = dimg;
    }

    public String getDrivercause() {
        return drivercause;
    }

    public void setDrivercause(String drivercause) {
        this.drivercause = drivercause;
    }

    public String getDriverphoto() {
        return driverphoto;
    }

    public void setDriverphoto(String driverphoto) {
        this.driverphoto = driverphoto;
    }

    public int getDveritystate() {
        return dveritystate;
    }

    public void setDveritystate(int dveritystate) {
        this.dveritystate = dveritystate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getInsuranceimg() {
        return insuranceimg;
    }

    public void setInsuranceimg(String insuranceimg) {
        this.insuranceimg = insuranceimg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReservephone() {
        return reservephone;
    }

    public void setReservephone(String reservephone) {
        this.reservephone = reservephone;
    }

    public String getRoadphoto() {
        return roadphoto;
    }

    public void setRoadphoto(String roadphoto) {
        this.roadphoto = roadphoto;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getTransportcertificate() {
        return transportcertificate;
    }

    public void setTransportcertificate(String transportcertificate) {
        this.transportcertificate = transportcertificate;
    }

    public String getWorkphoto() {
        return workphoto;
    }

    public void setWorkphoto(String workphoto) {
        this.workphoto = workphoto;
    }
}
