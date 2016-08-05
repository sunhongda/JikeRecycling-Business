package com.buslink.busjie.driver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.CircleTransform;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

public class JudgePassengerFragment extends LevelTwoFragment {

//    @ViewInject(R.id.passenger_star)
//    RatingBar ratingBar;
//    @ViewInject(R.id.passenger_star_textview)
//    TextView mStarNumber;
    @ViewInject(R.id.judge_star)
    RatingBar mBar2;
    @ViewInject(R.id.judge_edittext)
    EditText mEditText;
    @ViewInject(R.id.button)
    Button mButton;
    @ViewInject(R.id.tv_passage)
    TextView tvPassage;
    @ViewInject(R.id.iv)
    ImageView iv;

    private String orid;

    @OnClick(R.id.button)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                postJudge();
                break;
        }
    }

    @Override
    public String getTitle() {
        return "评价乘客";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_judge_passenger;
    }

    @Override
    protected void initView() {
        orid = mActivity.getIntent().getStringExtra(JsonName.ORID);

        mBar2.setRating(5);//设置初始5颗星

        String star = mActivity.getIntent().getStringExtra(JsonName.PSTAR);
//        ratingBar.setRating(TextUtils.isEmpty(star) ? 5 : Integer.parseInt(star));
//        mStarNumber.setText(star);
        tvPassage.setText(mActivity.getIntent().getStringExtra(JsonName.NAME));
//        BitmapUtils bitmapUtils=new BitmapUtils(getActivity());
//        bitmapUtils.display(iv, Net.IMGURL+mActivity.getIntent().getStringExtra(JsonName.IMG));

        Picasso.with(mActivity)
                .load(Net.IMGURL+mActivity.getIntent().getStringExtra(JsonName.IMG))
                .error(R.mipmap.home_wo_default)
                .transform(new CircleTransform())
                .into(iv);



        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mButton.setEnabled(true);
                } else {
                    mButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);

    }

    private void postJudge() {
        if (mBar2.getRating() == 0) {
            mActivity.app.toast("请给乘客评个分");
            return;
        }
        if (TextUtils.isEmpty(mEditText.getText()==null?"":mEditText.getText().toString().trim())) {
            mActivity.app.toast("请输入对乘客的评价");
            return;
        }
        RequestParams params = RequestManager.simpleGetParams();
        params.addQueryStringParameter(JsonName.ORID, orid);
        params.addQueryStringParameter(JsonName.STAR, mBar2.getProgress() + "");
        params.addQueryStringParameter(JsonName.UID,mActivity.getIntent().getStringExtra(JsonName.UID));
        params.addQueryStringParameter(JsonName.VALUATE, mEditText.getText().toString());
        params.addQueryStringParameter(JsonName.DID,mActivity.getIntent().getStringExtra(JsonName.DID));
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.JUDGEPASSENGER, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                hideInputMethod();
                try {
                    JSONObject jo = new JSONObject(responseInfo.result);
                    boolean status = XString.getBoolean(jo, "status");
                    JSONObject data = XString.getJSONObject(jo, "data");
                    if (status) {
                        mActivity.app.toast("评价成功");
                        EventBus.getDefault().post("upDateTripDetail","tripDetal");
                        mActivity.finish();
                    } else {
                        mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mActivity.app.toast("很抱歉，提交评价失败");
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.e(s, e);
                hideInputMethod();
                mActivity.app.toast("很抱歉，提交评价失败");
            }
        });
    }

    private void hideInputMethod() {
        InputMethodManager mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
