package com.buslink.busjie.driver.util;

import com.buslink.busjie.driver.R;

/**
 * Created by Administrator on 2015/9/10.
 */
public class AppUtils {
    public static String getAddress(String province, String city, String address) {
        if (address == null) {
            address = "";
        }
        if (!address.contains(city)) {
            address = city + address;
        }
        if (!address.contains(province)) {
            address = province + address;
        }
        return address;
    }
//    public static String getTicketsState(int tickateState){
//
//    }


    public static String getOrderState(int orderState) {
        String s = "";
        switch (orderState) {
            case 1:
                s = "未抢单";
                break;
            case 2:
                s = "已报价";
                break;
            case 3:
                s = "已支付";
                break;
            case 4:
                s = "已完成";
                break;
            case 5:
                s = "已评价";
                break;
            case 6:
                s = "被抢单";
                break;
            case 7:
                s = "无效";
                break;
            case 8:
                s = "线下支付";
                break;
            case 9:
                s = "已接单";
                break;
            case 10:
                s = "过期";
                break;
            case 11:
                s="已退款";
                break;
            case 12:
                s="已取消";
                break;

            case 13:
                s="未接单";
                break;
            case 14:
                s="未支付";
                break;

        }
        return s;
    }
    public static int getOrderTypeIcon(int orderType) {
        switch (orderType) {

            // 订单类型 
            // 1接/送飞机 2广告单 3接/送火车 
            // 5旅游包车 6单位班车 7 会议用车 
            // 8预约巴士
            case 1:
                return R.mipmap.icon_order_plan;
            case 2:
                return R.mipmap.icon_order_bus;
            case 3:
                return R.mipmap.icon_order_train;
            case 4:
            case 5:
                return R.mipmap.icon_order_bus;
            case 6:
                return R.mipmap.icon_order_car;
            case 7:
                return R.mipmap.icon_order_meeting_car;
            case 8:
                return R.mipmap.icon_order_appointment_bus;
            case 10:
                return R.mipmap.icon_order_character;
            default:
                return R.mipmap.icon_order_bus;
        }
    }




    public static String getOrderTypeString(int orderType) {
        switch (orderType) {

            // 订单类型 
            // 1接/送飞机 2广告单 3接/送火车 
            // 5旅游包车 6单位班车 7 会议用车 
            // 8预约巴士
            case 1:
                return "接/送飞机";
            case 2:
                return "广告车";
            case 3:
                return "接/送火车";
            case 4:
            case 5:
                return "旅游包车";
            case 6:
                return "单位班车";
            case 7:
                return "会议用车";
            case 8:
                return "预约巴士";
            case 9:
                return "旅游包车";
            case 10:
                return "包车";
            default:
                return "巴士预约";
        }
    }
    public static String getPlay(int state) {
        String s = "";
        switch (state) {
            case 1:
                s = "支付宝";
                break;
            case 2:
                s = "微信";
                break;
            case 3:
                s = "线下划款";
                break;
            case 4:
                s = "银行卡支付";
                break;
            case 5:
                s = "零钱支付";
                break;
        }
        return s;
    }

    public static String getGender(int state) {
        String s = "";
        switch (state) {
            case 1:
                s = "男";
                break;
            case 2:
                s = "女";
                break;
            default:
                s = "男";
                break;
        }
        return s;
    }

    public static String getCarType(int car) {
        String s = null;
        switch (car) {
            case 1:
                s = "普通型";
                break;
            case 2:
                s = "舒适型";
                break;
            case 3:
                s = "豪华型";
                break;
            default:
                s="普通型";
                break;
        }
        return s;
    }

    public static String getResourceType(int type){
        switch (type){
            case 1:
                return "红包";
            case 2:
                return "提现";
            case 3:
                return "充值";
            case 4:
                return "支出";
            case 5:
                return "收益";
            default:
                return "";
        }
    }
    public static String getPayType(int type){
        switch (type){
            case 1:
                return "+";
            case 2:
                return "-";
            default:
                return "";
        }
    }


    public static int getResourceTypeIocn(int type) {
        switch (type) {
            case 1:
                //红包
                return R.mipmap.icon_hong;
            case 2:
                //提现
                return R.mipmap.icon_ti;
            case 3:
                //充值
                return R.mipmap.icon_chong;
            case 4:
                //支出
                return R.mipmap.icon_zi;
            case 5:
                //收益
                return R.mipmap.icon_shou;
            case 6:
                //退款
                return R.mipmap.icon_tui;
            default:
                return R.mipmap.icon_shou;
        }
    }

    public static String getResourceTypeText(int type) {
        switch (type) {
            case 1:
                return "处理中...";
//            case 2:
//                return "提现";
            case 3:
                return "已入账";
            case 4:
                return "线下交易";
//            case 5:
//                return "红包收入";
            case 6:
                return "订单补偿金";
            default:
                return "已入账";
        }
    }




}
