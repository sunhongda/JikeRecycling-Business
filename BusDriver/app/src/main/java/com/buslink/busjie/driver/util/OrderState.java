package com.buslink.busjie.driver.util;

/**
 * Created by Administrator on 2015/9/12.
 */
public class OrderState {

    public static String getState(int orderState) {
        switch (orderState) {
            case 1:
                return "未报价";
            case 2:
                return "已报价";
            case 3:
                return "已支付";
            case 4:
                return "已完成";
            case 5:
                return "已评价";
            case 6:
                return "被抢单";
            case 7:
                return "无效";
            case 8:
                return "现金支付";
            case 9:
                return "已接单";
            case 10:
                return "过期";
            default:
                return "";
        }
    }

}
