package com.buslink.busjie.driver.constant;

import com.buslink.busjie.driver.R;

/**
 * Created by Administrator on 2015/9/18.
 */
public class BankIcon {

    public static int getBankIcon(String bankName) {
        switch (bankName) {
            case "中国银行":
                return R.mipmap.icon_zhongguo;
            case "工商银行":
                return R.mipmap.icon_gongshang;
            case "建设银行":
                return R.mipmap.icon_jianshe;
            case "交通银行":
                return R.mipmap.icon_jiaotong;
            case "农业银行":
                return R.mipmap.icon_nonye;
            case "邮政银行":
                return R.mipmap.icon_youzheng;
            case "中信银行":
                return R.mipmap.icon_zhongxin;
            case "光大银行":
                return R.mipmap.icon_guangda;
            case "民生银行":
                return R.mipmap.icon_minsheng;
            case "华夏银行":
                return R.mipmap.icon_huaxia;
            case "广发银行":
                return R.mipmap.icon_guangfa;
            case "深圳发展银行":
                return R.mipmap.icon_shenfa;
            case "招商银行":
                return R.mipmap.icon_zhaoshang;
            case "兴业银行":
                return R.mipmap.icon_xingye;
            case "浦发银行":
                return R.mipmap.icon_pufa;
            case "平安银行":
                return R.mipmap.icon_pingan;
            case "北京银行":
                return R.mipmap.icon_beijing;
            case "北京农商银行":
                return R.mipmap.icon_beijingnonshang;
            default:
                return 0;
        }
    }

}
