package com.buslink.busjie.driver.entity;

import java.io.Serializable;

/**
 * Created by yingpeng.cao on 2015/6/22.
 */
public class MapSelectAdressInfo implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6143151645955967264L;
	
//	public String mName;
//    public String mAdress;
//    public String mDescript;
//    public String mCity;
//    public String mProvince;
//    public String mPostCode;
//    public String lon,lat;




    public String mName;
    public String mAdress;
    public String mDescript;
    public String mCity;
    public String mProvince;
    public String mPostCode;
    public String lon,lat;

    public MapSelectAdressInfo() {

    }


    public MapSelectAdressInfo(String mName, String mAdress, String mDescript, String mCity, String mProvince, String mPostCode, String lon, String lat) {
        this.mName = mName;
        this.mAdress = mAdress;
        this.mDescript = mDescript;
        this.mCity = mCity;
        this.mProvince = mProvince;
        this.mPostCode = mPostCode;
        this.lon = lon;
        this.lat = lat;
    }


    public String getmName() {
        return mName;
    }

    public String getmAdress() {
        return mAdress;
    }

    public String getmDescript() {
        return mDescript;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmProvince() {
        return mProvince;
    }

    public String getmPostCode() {
        return mPostCode;
    }

    public String getLon() {
        return lon;
    }

    public String getLat() {
        return lat;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmAdress(String mAdress) {
        this.mAdress = mAdress;
    }

    public void setmDescript(String mDescript) {
        this.mDescript = mDescript;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public void setmProvince(String mProvince) {
        this.mProvince = mProvince;
    }

    public void setmPostCode(String mPostCode) {
        this.mPostCode = mPostCode;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
