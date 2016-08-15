package com.angm.jikeb.util;

public class SdCardInfo {
    public enum SDCardType {
	INNERCARD, EXTERNALCARD
    }

    /*
     * SDK小于12，系统不区分内置位置卡 SDK大于等于12, 根据sd卡是否可以卸载区分内置外置卡
     */
    public SDCardType isExternalCard; // 是否为外置卡 0:内置卡、1：外置卡、2：无法区分
    public String path = null; // sd卡路径
    public String totalSize;    //sd 卡的总大小
    public String usedSize;    // sd 卡的已使用 大小
    public String availableSize;    //sd卡 的可使用大小

    public SdCardInfo() {
    }

    public SdCardInfo(SDCardType isExternalCard, String path) {
	super();
	this.isExternalCard = isExternalCard;
	this.path = path;
    }

}
