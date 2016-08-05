package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.MyHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2015/9/23.
 */
public class AgreementFragment extends LevelTwoFragment {

    @ViewInject(R.id.agreement)
    private TextView mAgreement;

    @Override
    public String getTitle() {
        return "司机协议";
    }

    @Override
    protected int getResLayout() {
        return R.layout.agreement;
    }

    @Override
    protected void initView() {
        try {
            InputStream is = mActivity.getAssets().open("driver_agreement.txt");
            String message = MyHelper.getStringFromInputStream(is);
            mAgreement.setMovementMethod(new ScrollingMovementMethod());
            mAgreement.setText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }
}
