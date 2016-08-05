package com.buslink.busjie.driver.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.PickPhotoActivity;
import com.buslink.busjie.driver.activity.SignInActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.base.DrawerActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.User;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.DbManager;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.response.HomeResponse;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class DrawerFragment extends Fragment {
    @ViewInject(R.id.headimg)
    ImageView mHeadImg;
    @ViewInject(R.id.name)
    TextView mName;
    @ViewInject(R.id.ratingbar)
    RatingBar mRatingBar;
    DrawerActivity activity;
    DrawerLayout mDrawerLayout;
    private HomeResponse homeResponse = new HomeResponse();

   @OnClick({R.id.headimg,
           R.id.user,
           R.id.bus,
           R.id.purse,
          // R.id.history,
           R.id.share,
           R.id.busbond,
           R.id.setting,
           R.id.exit})
   private void onclick(View v) {
       switch (v.getId()) {
           case R.id.headimg:
           case R.id.user:
               Intent intent = new Intent();
               intent.putExtra(MyHelper.homeToDrawerToUserInfo, formJson());
               activity.startFragment(BackActivity.class, UserInfoFragment.class, intent);
               break;
           case R.id.bus:
               startFragment(BackActivity.class, BusInfoFragment.class);
               break;
//           case R.id.history:
//               startFragment(BackActivity.class,AdListFragment2.class);
//               break;
           case R.id.purse:
               RequestManager.myWallet(new MyWalletRequestCallBack());
               break;
           case R.id.share:
               startFragment(BackActivity.class, ShareFragment.class);
               break;
           case R.id.busbond:
               startFragment(BackActivity.class, BusFragment.class);
               break;
           case R.id.setting:
               startFragment(BackActivity.class, SettingFragment.class);
               break;
           case R.id.exit:
               exit();
               break;
       }
   }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (DrawerActivity) activity;
        mDrawerLayout = this.activity.getmDrawerLayout();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, null);
        ViewUtils.inject(this, view);
        return view;
    }

    @Subscribe
    public void onEvemt(MyEvent e) {
        if (TextUtils.equals(e.getTag(), EventName.updateMenuUI)) {
            homeResponse = (HomeResponse) e.getExtra("response");
            updateUI();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class MyWalletRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            try {
                JSONObject result = new JSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                if (XString.getBoolean(result, JsonName.STATUS)) {
                    Intent intent = new Intent(getActivity(), BackActivity.class);
                    intent.putExtra("wallet",responseInfo.result);
                    if (XString.getInt(data, JsonName.IS_SET_UPPWD) != 0) {
                        intent.putExtra("fragmentName", PassInputFragment.class.getName());
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "setup_password");
                    } else {
                        intent.putExtra("wallet",responseInfo.result);
                        intent.putExtra("fragmentName", PurseFragment.class.getName());
                    }
                    startActivity(intent);
                } else {
                    activity.app.toast(XString.getStr(data, "msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            LogUtils.e(s, e);
        }
    }

    private void updateUI() {
        String headUrl = homeResponse.getImg();
        String name = homeResponse.getName();
        int star = homeResponse.getStar();
        MyHelper.setRoundImage(activity, mHeadImg, headUrl, R.mipmap.head_ring);
        mName.setText(name);
        mRatingBar.setRating(star);
    }

    private void replaceFragment(Fragment fragment) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        activity.replaceFragment(fragment);
    }

    private void startFragment(Class<?> activity, Class<? extends LevelTwoFragment> fragment) {
        this.activity.startFragment(activity, fragment);
    }

    private String formJson() {
        try {
            JSONObject jo = new JSONObject();
            jo.put("ordersum", homeResponse.getOrdersum());
            jo.put("ranking", homeResponse.getRanking());
            jo.put("sum", homeResponse.getSum());
            jo.put("star", homeResponse.getStar());
            return jo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void exit() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("是否确认退出？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbUtils db = DbManager.getDb();
                        try {
                            sendExit();//先退出 需要uid
                            db.deleteAll(User.class);
                            File file = new File(PickPhotoActivity.OUT_FILE_DIR);
                            if (file.exists()) {
                                deleteFile(file.getParentFile());
                            }
                            activity.app.exit();
                            startActivity(new Intent(activity, SignInActivity.class));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    private void sendExit() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = MyHelper.getParams();
        params.add(JsonName.OPERATIONTYPE, "2");//1进入2退出
        //退出登录
        client.post(activity, Net.LONINSTATEHANDLE, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }
    private void deleteFile(File oldPath) {
        if (oldPath.isDirectory()) {
            File[] files = oldPath.listFiles();
            for (File file: files) {
                deleteFile(file);
            }
            oldPath.delete();
        } else {
            oldPath.delete();
        }
    }
    public void switchContent(LevelOneFragment to) {
        if (activity.mContent != to) {
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(activity.mContent).add(R.id.content_frame, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(activity.mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            activity.mContent = to;
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

}
