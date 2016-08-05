package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.view.View;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.view.InputListener;
import com.buslink.busjie.driver.view.PassInputView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/9/19.
 */
public class CheckPassFragment extends LevelTwoFragment {

    @ViewInject(R.id.piv)
    private PassInputView piv;

    @Override
    public String getTitle() {
        return "输入密码";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_pass_input;
    }

    protected void initView(){
        piv.setIsOne(true);
        piv.setListener(new InputListener() {
            @Override
            public void onErr() {

            }

            @Override
            public void onCancle() {
                mActivity.finish();
            }

            @Override
            public void onTrue(String tv) {
                MyEvent e=new MyEvent("checkPass");
                e.setData(tv);
                EventBus.getDefault().post(e);
                mActivity.finish();
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }
}
