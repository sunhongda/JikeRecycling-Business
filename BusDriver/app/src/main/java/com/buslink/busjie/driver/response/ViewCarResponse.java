package com.buslink.busjie.driver.response;

import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/26.
 *
 */
public class ViewCarResponse extends ParseJSON {

    private String brand;
    private String carimg;
    private String carnumber;
    private int cartype;
    private String city;
    private String insuranceimg;
    private String roadphoto;
    private long carbuydate;
    private int seat;
    private String transportcertificate;
    private int veritystate;

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
        carimg = XString.getStr(data, "carimg");
        carnumber = XString.getStr(data, "carnumber");
        carbuydate = XString.getLong(data, "carbuydate");
        city = XString.getStr(data, "city");
        insuranceimg = XString.getStr(data, "insuranceimg");
        roadphoto = XString.getStr(data, "roadphoto");
        seat = XString.getInt(data, "seat");
        transportcertificate = XString.getStr(data, "transportcertificate");
        veritystate = XString.getInt(data, "veritystate");
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCarimg(String carimg) {
        this.carimg = carimg;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public void setCartype(int cartype) {
        this.cartype = cartype;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setInsuranceimg(String insuranceimg) {
        this.insuranceimg = insuranceimg;
    }

    public void setRoadphoto(String roadphoto) {
        this.roadphoto = roadphoto;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public void setTransportcertificate(String transportcertificate) {
        this.transportcertificate = transportcertificate;
    }

    public void setVeritystate(int veritystate) {
        this.veritystate = veritystate;
    }

    public String getBrand() {
        return brand;
    }

    public String getCarimg() {
        return carimg;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public int getCartype() {
        return cartype;
    }

    public String getCity() {
        return city;
    }

    public String getInsuranceimg() {
        return insuranceimg;
    }

    public String getRoadphoto() {
        return roadphoto;
    }

    public int getSeat() {
        return seat;
    }

    public String getTransportcertificate() {
        return transportcertificate;
    }

    public int getVeritystate() {
        return veritystate;
    }


    public long getCarbuydate() {
        return carbuydate;
    }

    public void setCarbuydate(long carbuydate) {
        this.carbuydate = carbuydate;
    }

}
