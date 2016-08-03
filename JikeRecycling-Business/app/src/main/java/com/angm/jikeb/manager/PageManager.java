package com.angm.jikeb.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.angm.jikeb.activity.MainActivity;
import com.angm.jikeb.page.BasePage;


public class PageManager {
	private MainActivity activity;
	private LayoutInflater mInflater;
	private Context mContext;

	public PageManager(MainActivity activity     ) {
		this.activity = activity;
		mContext=(Context)activity;
		mInflater = LayoutInflater.from((Context)activity);

	}

	public PageObject createPage(int index) {
	/*	BasePage page = null;
		View view = null;
		switch (index) {
		case Configs.VIEW_POSITION_MAIN: {
			view = mInflater.inflate(R.layout.layout_main1, null);
			page = new MainPage(mContext, view, mActivityInterface);
			break;
		}

		if (page == null || view == null)
			throw new IllegalArgumentException(
					"the Page is null or the View is null.");
		return new PageObject(index, view, page);*/
		return  null;
	}

	  class PageObject {
		  private View a;
		  private BasePage b;
		  private int c;

		  public PageObject(int var1, View var2, BasePage var3) {
			  this.c = var1;
			  this.a = var2;
			  this.b = var3;
		  }

		  public View getView() {
			  return this.a;
		  }

		  public BasePage getPage() {
			  return this.b;
		  }

		  public int getPosition() {
			  return this.c;
		  }

		  public void destroy() {
			  this.b.onDestroy();
			  this.a = null;
			  this.b = null;
		  }
	  }
}
