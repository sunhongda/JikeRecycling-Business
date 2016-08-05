package com.buslink.busjie.driver.util;


import android.support.annotation.Keep;

import java.io.Serializable;

@Keep
public enum PhotoSelectOptions implements Serializable{
	DEFALUT, TAKE_PHOTO_BY_CAMERA, SELECT_PHOTO_FROM_GALLARY
}
