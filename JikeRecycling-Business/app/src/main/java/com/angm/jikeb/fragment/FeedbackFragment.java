package com.angm.jikeb.fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.angm.jikeb.R;

import butterknife.Bind;

/**
 * Created by shd on 16-8-8.
 * 意见反馈Fragment
 */
public class FeedbackFragment extends LevelTwoFragment {
    MenuItem item;
    @Bind(R.id.feedback_suggestion_edittext)
    EditText et;

    @Override
    public String getTitle() {
        return "意见反馈";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_feedback;
    }

    @Override
    protected void initView() {
    /*    mToolbar.inflateMenu(R.menu.menu_feedback);
        item = mToolbar.getMenu().getItem(0);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ad_history:
                        if (verify()) {
                            RequestParams params = new RequestParams();
                            params.addQueryStringParameter("content", et.getText().toString());
                            RequestManager.feedBack(params, new FeedBackRequestCallBack());
                            break;
                        }
                }
                return false;
            }
        });*/
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void hideInputMethod() {
        InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


/*
    class FeedBackRequestCallBack extends RequestCallBack<String> {

        @Override
        public void onStart() {
            hideInputMethod();
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                boolean status = jo.getBoolean("status");
                if (status) {
                    mActivity.app.toast("反馈成功!");
                    mActivity.finish();
                } else {
                    mActivity.app.toast("很抱歉，反馈失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mActivity.app.toast("很抱歉，反馈失败");
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.app.toast("您的网络不给力");
        }
    }
*/

    private boolean verify() {
        if (TextUtils.isEmpty(et.getText().toString())) {
            mActivity.app.toast("内容不能为空");
            return false;
        }
        return true;
    }
}
