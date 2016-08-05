package com.buslink.busjie.driver.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.db.UserHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingFragment extends LevelTwoFragment {

    @Bind(R.id.sound)
    SwitchCompat sound;

    boolean checked = false;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @OnClick(R.id.sound_ll) void sound() {
        checked = !checked;
        sound.setChecked(checked);
    }

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {
        sound.setChecked(checked);
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                editor.putBoolean("sound", isChecked);
                editor.commit();
            }
        });
    }

    private void initData() {
        sp = mActivity.getSharedPreferences(UserHelper.getInstance().getUid(), Context.MODE_PRIVATE);
        editor = sp.edit();
        checked = sp.getBoolean("sound", true);
        editor.apply();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
