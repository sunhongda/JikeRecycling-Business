package com.angm.jikeb.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AppInfo implements Serializable {
	
	private String name;       //应用名
	private String version;    //版本
	private String packageName;//包名
	private MyBitmap icon;	   //图标
	private String auth_end;   //授权结束时间
	private boolean isAuths;   // 是否授权
	private boolean isSD;      // 是否在sd卡安装	
	private int unInsOrIns;    //1卸载或2安装
	
	private boolean isAppStore;//是否是应用商店应用
	private boolean isAddMore; //是否添加到更多界面
	private boolean isCustom; //是否是定制应用
	
	private String iconPath; //图片路径
	
	public String getIconPath() {
		return iconPath;
	}
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}
	public boolean isCustom() {
		return isCustom;
	}
	public void setCustom(boolean isCustom) {
		this.isCustom = isCustom;
	}
	public String getAuth_end() {
		return auth_end;
	}
	public void setAuth_end(String auth_end) {
		this.auth_end = auth_end;
	}
	public String getAuth_start() {
		return auth_start;
	}
	public void setAuth_start(String auth_start) {
		this.auth_start = auth_start;
	}
	private String auth_start;
	
	public MyBitmap getIcon() { 
		return icon;
	}
	public void setIcon(MyBitmap icon) {
		this.icon = icon;
	}
	public boolean isAppStore() {
		return isAppStore;
	}
	public void setAppStore(boolean isAppStore) {
		this.isAppStore = isAppStore;
	}
	public boolean isAddMore() {
		return isAddMore;
	}
	public void setAddMore(boolean isAddMore) {
		this.isAddMore = isAddMore;
	}
	public boolean isAuths() {
		return isAuths;
	}
	
	/**
	 * 
	 * @param unInsOrIns  //1卸载或2安装
	 */
	public int getUnInsOrIns() {
		return unInsOrIns;
	}
	/**
	 * 
	 * @param unInsOrIns  //1卸载或2安装
	 */
	public void setUnInsOrIns(int unInsOrIns) {
		this.unInsOrIns = unInsOrIns;
	}
	/**
	 * @return the isAuths
	 */
	public boolean getAuths() {
		return isAuths;
	}
	/**
	 * @param isAuths the isAuths to set
	 */
	public void setAuths(boolean isAuths) {
		this.isAuths = isAuths;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", version=" + version
				+ ", packageName=" + packageName + ", icon=" + icon
				+ ", auth_end=" + auth_end + ", isAuths=" + isAuths + ", isSD="
				+ isSD + ", unInsOrIns=" + unInsOrIns + ", isAppStore="
				+ isAppStore + ", isAddMore=" + isAddMore + ", isCustom="
				+ isCustom + ", iconPath=" + iconPath + ", auth_start="
				+ auth_start + "]";
	}
}
