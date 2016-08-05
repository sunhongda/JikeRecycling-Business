package com.buslink.busjie.driver.response;

import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/16.
 * 接口39
 */
public class HomeResponse extends ParseJSON{

    private String cid;
    private String company;
    private String dstate;
    private String img;
    private int iscontrol;
    private int isorder;
    private int ispushad;
    private String isreg;
    private int istrip;
    private int iscarpooling;
    private String name;
    private int ordersum;
    private int ranking;
    private int star;
    private String state;
    private int sum;
    private String uid;
    private String veritystate;

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
        cid = XString.getStr(data, "cid");
        company = XString.getStr(data, "company");
        dstate = XString.getStr(data, "dstate");
        img = XString.getStr(data, "img");
        iscontrol = XString.getInt(data, "iscontrol");
        isorder = XString.getInt(data, "isorder");
        ispushad = XString.getInt(data, "ispushad");
        isreg = XString.getStr(data, "isreg");
        istrip = XString.getInt(data, "istrip");
        iscarpooling=XString.getInt(data,"iscarpooling");
        name = XString.getStr(data, "name");
        ordersum = XString.getInt(data, "ordersum");
        ranking = XString.getInt(data, "ranking");
        star = XString.getInt(data, "star");
        state = XString.getStr(data, "state");
        sum = XString.getInt(data, "sum");
        uid = XString.getStr(data, "uid");
        veritystate = XString.getStr(data, "veritystate");
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDstate() {
        return dstate;
    }

    public void setDstate(String dstate) {
        this.dstate = dstate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getIscontrol() {
        return iscontrol;
    }

    public void setIscontrol(int iscontrol) {
        this.iscontrol = iscontrol;
    }

    public int getIsorder() {
        return isorder;
    }

    public void setIsorder(int isorder) {
        this.isorder = isorder;
    }

    public int getIspushad() {
        return ispushad;
    }

    public void setIspushad(int ispushad) {
        this.ispushad = ispushad;
    }

    public String getIsreg() {
        return isreg;
    }

    public void setIsreg(String isreg) {
        this.isreg = isreg;
    }

    public int getIstrip() {
        return istrip;
    }
    public void setIstrip(int istrip) {
        this.istrip = istrip;
    }

    public int getIscarpooling(){
        return iscarpooling;
    }

    public void setIscarpooling(int iscarpooling) {
        this.iscarpooling = iscarpooling;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrdersum() {
        return ordersum;
    }

    public void setOrdersum(int ordersum) {
        this.ordersum = ordersum;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVeritystate() {
        return veritystate;
    }

    public void setVeritystate(String veritystate) {
        this.veritystate = veritystate;
    }
}
