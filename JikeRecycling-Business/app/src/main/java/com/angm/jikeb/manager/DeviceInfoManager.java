package com.angm.jikeb.manager;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by yanlong.luo on 2015/9/7.
 */
public class DeviceInfoManager {

    private static Context mContext;

    static {
        mContext = MyApplication.getApplication();
    }

    public static String getDeviceId() {
        TelephonyManager mTelephonyManager = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getDeviceId();
    }

}
