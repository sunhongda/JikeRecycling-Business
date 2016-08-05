package com.buslink.busjie.driver.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.NoBackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.UserConstant;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.fragment.CarPhotoFragment;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.CameraUtil;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.PhotoSelectOptions;
import com.buslink.busjie.driver.util.ToastHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
            //注册页
public class SignUpActivity extends AppCompatActivity {

    @ViewInject(R.id.first_login_layout)
    LinearLayout mRootLayout1;

    @ViewInject(R.id.signup1_scrollview)
    ScrollView mScrollView1;

    @ViewInject(R.id.signup1_driver_name)
    EditText mNameEditText;

    @ViewInject(R.id.signup1_driver_company)
    EditText mCompanyEditText;

    @ViewInject(R.id.signup1_driver_backup_phone)
    EditText mBackupPhone;

    @ViewInject(R.id.signup1_sex_radio_group)
    RadioGroup mSexRadioGroup;

    @ViewInject(R.id.signup1_next_btn)
    Button mNextBtn;

    @ViewInject(R.id.signup1_driver_head)
    ImageView mHead;

    @ViewInject(R.id.sfz)
    ImageView sfz;

    @ViewInject(R.id.sfz_loading)
    ImageView mSfzLoading;

    @ViewInject(R.id.signup1_driver_licence)
    ImageView jsz;

    @ViewInject(R.id.signup1_driver_jop_card)
    ImageView cyzgz;

    @ViewInject(R.id.signup1_driver_head_loading)
    ImageView headLoading;

    @ViewInject(R.id.signup1_driver_licence_loading)
    ImageView jszLoading;

    @ViewInject(R.id.signup1_driver_jop_card_loading)
    ImageView cyzgzLoading;

    @ViewInject(R.id.second_login_layout)
    LinearLayout mRootLayout2;

    @ViewInject(R.id.signup2_scrollview)
    ScrollView mScrollView2;

    @ViewInject(R.id.car_licence_prefix_tv)
    TextView mLicencePrefix;

    @ViewInject(R.id.car_licence_postfix)
    EditText mLicencePostfix;

    @ViewInject(R.id.signup2_bus_city_textview)
    TextView mBusCity;

    @ViewInject(R.id.signup2_bus_brand_textview)
    TextView mBusBrand;

    @ViewInject(R.id.signup2_bus_seatnum_edittext)
    EditText mBusSeatNum;

    @ViewInject(R.id.signup2_bus_icon_imageview)
    ImageView mBusIcon;

    @ViewInject(R.id.signup2_insurance_imgview)
    ImageView mInsurance;

    @ViewInject(R.id.signup2_bus_type_textview)
    TextView mBusType;

    @ViewInject(R.id.bus_info_bus_loading)
    ImageView busLoading;

    @ViewInject(R.id.signup2_insurance_imgview_loading)
    ImageView insuranceLoading;

    @ViewInject(R.id.xsz)
    ImageView xsz; // 行驶证

    @ViewInject(R.id.xsz_loading)
    ImageView xszLoading;

    @ViewInject(R.id.signup2_jyxkz)
    ImageView jyxkz;

    @ViewInject(R.id.signup2_jyxkz_loading)
    ImageView jyxkzLoading;

    private int mLoginState = 1;
    private boolean isWarnedToClose = false;

    private String headPathOnSaveValue, licencePathOnSaveValue, jobcardPathOnSaveValue;
    private String busPathOnSaveValue, insurancePathOnSaveValue, routePathOnSaveValue;

    private final String headPathOnSaveKey = "head";
    private final String licencePathOnSaveKey = "licence";
    private final String jobcardPathOnSaveKey = "jobcard";
    private final String busPathOnSaveKey = "bus";
    private final String insurancePathOnSaveKey = "insurance";
    private final String routePathOnSaveKey = "route";

    private final String headTagOnSaveKey = "headTag";
    private final String licenceTagOnSaveKey = "licenceTag";
    private final String jobcardTagOnSaveKey = "jobcardTag";
    private final String busTagOnSaveKey = "busTag";
    private final String insuranceTagOnSaveKey = "insuranceTag";
    private final String routeTagOnSaveKey = "routeTag";
    private String sex;
    private MyApplication app;
    public ProgressDialog dialog;
    private Bitmap sjtxBitmap, sfzBitmap, jszBitmap, cyzgzBitmap;
    private Bitmap ctxBitmap, bxzBitmap, xszBitmap, jyxkzBitmap;

    @OnFocusChange({R.id.signup1_driver_name,
            R.id.signup1_driver_company,
            R.id.signup1_driver_backup_phone,
            R.id.car_licence_postfix,
            R.id.signup2_bus_seatnum_edittext})
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            smoothScrollToView(v);
        }
    }

    @OnClick({R.id.signup1_driver_name,
            R.id.signup1_driver_company,
            R.id.signup1_driver_backup_phone,
            R.id.signup1_next_btn,
            R.id.head_photo_ll,
            R.id.sfz_photo_ll,
            R.id.licence_photo_ll,
            R.id.jobcard_photo_ll,
            R.id.signup1_driver_jop_card,
            R.id.car_licence_postfix,
            R.id.choose_bus_photo,
            R.id.bxz_ll,
            R.id.xsz_ll,
            R.id.jyxkz_photo_ll,
            R.id.signup2_bus_city_textview,
            R.id.signup2_bus_brand_textview,
            R.id.signup2_bus_seatnum_edittext,
            R.id.signup2_complete_btn,
            R.id.signup2_bus_type_textview}) // 从业资格证
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup1_driver_name:
                smoothScrollToView(v);
                break;
            case R.id.signup1_driver_company:
                smoothScrollToView(v);
                break;
            case R.id.signup1_driver_backup_phone:
                smoothScrollToView(v);
                break;
            case R.id.signup1_next_btn:
                hideInputMethod();
                if (!verifyDriver()) {
                    return;
                }

                RequestParams params1 = RequestManager.simplePostParams();
                params1.addBodyParameter("nickname", mNameEditText.getText().toString());
                switch (mSexRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.signup1_driver_radiomale:
                        params1.addBodyParameter("sex", "1");
                        sex = "1";
                        break;
                    case R.id.signup1_driver_radiofemale:
                        params1.addBodyParameter("sex", "2");
                        sex = "2";
                        break;
                }
                params1.addBodyParameter("company", mCompanyEditText.getText().toString());
                if (TextUtils.isEmpty(mBackupPhone.getText().toString())) {
                    params1.addBodyParameter("reservephone", "无");
                } else {
                    params1.addBodyParameter("reservephone", mBackupPhone.getText().toString());
                }
                params1.addBodyParameter("bdappid", UserHelper.getInstance().getBdappid());
                params1.addBodyParameter("bduserid", UserHelper.getInstance().getBduserid());
                params1.addBodyParameter("bdchannelid", UserHelper.getInstance().getBdchannelid());
                RequestManager.updateDriver(params1, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        dialog.dismiss();
                        mLoginState = 2;
                        mRootLayout1.setVisibility(View.GONE);
                        mRootLayout2.setVisibility(View.VISIBLE);
                        if (sjtxBitmap != null) {
                            sjtxBitmap.recycle();
                        }
                        if (sfzBitmap != null) {
                            sfzBitmap.recycle();
                        }
                        if (jszBitmap != null) {
                            jszBitmap.recycle();
                        }
                        if (cyzgzBitmap != null) {
                            cyzgzBitmap.recycle();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        dialog.dismiss();
                        app.toast("您的网络不给力");
                    }
                });
                break;
            case R.id.head_photo_ll:
                doPickPhotoAction(UserConstant.HEAD_REQUEST_CODE, UserConstant.HEAD_IMAGE_NAME);
                break;
            case R.id.sfz_photo_ll:
                doPickPhotoAction(UserConstant.SFZ_CODE, UserConstant.SFZ_NAME);
                break;
            case R.id.licence_photo_ll:
                doPickPhotoAction(UserConstant.LICENCE_REQUEST_CODE, UserConstant.LICENCE_IMAGE_NAME);
                break;
            case R.id.jobcard_photo_ll:
                doPickPhotoAction(UserConstant.JOBCARD_REQUEST_CODE, UserConstant.JOBCARD_IMAGE_NAME);
                break;
            case R.id.car_licence_postfix:
            case R.id.signup2_bus_seatnum_edittext:
                smoothScrollToView(v);
                break;
            case R.id.signup2_bus_city_textview:
                Intent busCityIntent = new Intent(this, ChooseProvinceActivity.class);
                startActivityForResult(busCityIntent, 0);
                break;
            case R.id.signup2_bus_brand_textview:
                Intent busBrandIntent = new Intent(this, BusBrandActivity.class);
                startActivityForResult(busBrandIntent, 0);
                break;
            case R.id.signup2_bus_type_textview:
                showDialog();
                break;
            case R.id.choose_bus_photo:
                doPickPhotoAction(UserConstant.BUS_REQUEST_CODE, UserConstant.BUS_IMAGE_NAME);
                break;
            case R.id.bxz_ll:
                doPickPhotoAction(UserConstant.INSURANCE_REQUEST_CODE, UserConstant.INSURANCE_IMAGE_NAME);
                break;
            case R.id.xsz_ll:
                doPickPhotoAction(UserConstant.XSZ_REQUEST_CODE, UserConstant.XSZ_NAME);
                break;
            case R.id.jyxkz_photo_ll:
                doPickPhotoAction(UserConstant.JYXKZ_CODE, UserConstant.JYXKZ_NAME);
                break;
            case R.id.signup2_complete_btn:
                if (!verifyBus()) {
                    return;
                }
                RequestParams params2 = RequestManager.simplePostParams();
                params2.addBodyParameter("carnumber", mLicencePrefix.getText().toString()
                        + mLicencePostfix.getText().toString());
                params2.addBodyParameter("city", mBusCity.getText().toString());
                params2.addBodyParameter("brand", mBusBrand.getText().toString());
                params2.addBodyParameter("seat", mBusSeatNum.getText().toString());
                params2.addBodyParameter("carbuydate", mBusType.getText().toString());
                RequestManager.updateCar(params2, new SignUpCallBack());
                break;
        }
    }

    @OnClick(R.id.car_licence_prefix)
    private void picker(View v) {
        startActivity(new Intent(this, ChooseCarActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EventBus.getDefault().register(this);
        ViewUtils.inject(this);
        app = MyApplication.getApp();
        initView();
        if (savedInstanceState != null) {
            String test1 = savedInstanceState.getString("test1");
            String headTag = savedInstanceState.getString(headTagOnSaveKey);
            if (headTag != null) {
                mHead.setTag(headTag);
                String headpath = savedInstanceState.getString("head");
                Bitmap headBitmap = CameraUtil.getCompressBitmap(headpath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                mHead.setImageBitmap(headBitmap);
            }
            String licenceTag = savedInstanceState.getString(licenceTagOnSaveKey);
            if (licenceTag != null) {
                jsz.setTag(licenceTag);
                String licencepath = savedInstanceState.getString("licence");
                Bitmap licenceBitmap = CameraUtil.getCompressBitmap(licencepath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                jsz.setImageBitmap(licenceBitmap);
            }
            String jobcardTag = savedInstanceState.getString(jobcardTagOnSaveKey);
            if (jobcardTag != null) {
                cyzgz.setTag(jobcardTag);
                String jobcardPath = savedInstanceState.getString("jobcard");
                Bitmap jobcardbitmap = CameraUtil.getCompressBitmap(jobcardPath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                cyzgz.setImageBitmap(jobcardbitmap);
            }
            String busTag = savedInstanceState.getString(busTagOnSaveKey);
            if (busTag != null) {
                mBusIcon.setTag(busTag);
                String busPath = savedInstanceState.getString("jobcard");
                Bitmap busBitmap = CameraUtil.getCompressBitmap(busPath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                mBusIcon.setImageBitmap(busBitmap);
            }
            String insuranceTag = savedInstanceState.getString(insuranceTagOnSaveKey);
            if (insuranceTag != null) {
                mInsurance.setTag(insuranceTag);
                String insurancePath = savedInstanceState.getString(insurancePathOnSaveKey);
                Bitmap insuranceBitmap = CameraUtil.getCompressBitmap(insurancePath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                mInsurance.setImageBitmap(insuranceBitmap);
            }
            String routeTag = savedInstanceState.getString(routeTagOnSaveKey);
            if (routeTag != null) {
                xsz.setTag(routeTag);
                String routePath = savedInstanceState.getString(routePathOnSaveKey);
                Bitmap routeBitmap = CameraUtil.getCompressBitmap(routePath, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                xsz.setImageBitmap(routeBitmap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mLoginState == 1) {
            if (isWarnedToClose) {
                finish();
            } else {
                isWarnedToClose = true;
                ToastHelper.showToast("再按一次退出");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isWarnedToClose = false;
                    }
                }, 2000);
            }
        } else if (mLoginState == 2) {
            mLoginState = 1;
            mRootLayout2.setVisibility(View.GONE);
            mRootLayout1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        String headPath = PickPhotoActivity.OUT_FILE_DIR + UserConstant.HEAD_IMAGE_NAME;
//        String licencePath = PickPhotoActivity.OUT_FILE_DIR + UserConstant.LICENCE_IMAGE_NAME;
//        String jobCardPath = PickPhotoActivity.OUT_FILE_DIR + UserConstant.JOBCARD_IMAGE_NAME;
        outState.putString("test1", "test1");
        if (mHead.getTag() != null) {
            outState.putString(headPathOnSaveKey, headPathOnSaveValue);
            outState.putString(headTagOnSaveKey, mHead.getTag().toString());
        }
        if (jsz.getTag() != null) {
            outState.putString(licencePathOnSaveKey, licencePathOnSaveValue);
            outState.putString(licenceTagOnSaveKey, jsz.getTag().toString());
        }
        if (cyzgz.getTag() != null) {
            outState.putString(jobcardPathOnSaveKey, jobcardPathOnSaveValue);
            outState.putString(jobcardTagOnSaveKey, cyzgz.getTag().toString());
        }
        if (mBusIcon.getTag() != null) {
            outState.putString(busPathOnSaveKey, busPathOnSaveValue);
            outState.putString(busTagOnSaveKey, mBusIcon.getTag().toString());
        }
        if (mInsurance.getTag() != null) {
            outState.putString(insurancePathOnSaveKey, insurancePathOnSaveValue);
            outState.putString(insuranceTagOnSaveKey, mInsurance.getTag().toString());
        }
        if (xsz.getTag() != null) {
            outState.putString(routePathOnSaveKey, routePathOnSaveValue);
            outState.putString(routeTagOnSaveKey, xsz.getTag().toString());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if (TextUtils.equals(e.getTag(), EventName.ChooseCarLicence)) {
            String province = (String) e.getExtra("province");
            String alphabet = (String) e.getExtra("alphabet");
            mLicencePrefix.setText(province + alphabet);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f); // 设置旋转动画
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);
        switch (resultCode) {
            case 1: // PickPhotoActivity返回
                String result = data.getStringExtra(PickPhotoActivity.PHOTO_PATH);
                if (!TextUtils.isEmpty(result)) {
                    //Bitmap bitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("isupdate", "0");
                    params.addBodyParameter("avatar", new File(result));
                    switch (requestCode) {
                        case UserConstant.HEAD_REQUEST_CODE:
                            sjtxBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            mHead.setImageBitmap(sjtxBitmap);
                            mHead.setTag("upload");
                            headPathOnSaveValue = result;
                            headLoading.setVisibility(View.VISIBLE);
                            headLoading.setAnimation(animation);
                            ImageUploadCallBack headCallBack = new ImageUploadCallBack(mHead, headLoading);
                            params.addBodyParameter("imgtype", "2");
                            params.addBodyParameter("key", UserHelper.getInstance().getUid());
                            RequestManager.imageUpload(params, headCallBack);
                            break;
                        case UserConstant.SFZ_CODE:
                            sfzBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            sfz.setImageBitmap(sfzBitmap);
                            sfz.setTag("upload");
                            mSfzLoading.setVisibility(View.VISIBLE);
                            mSfzLoading.setAnimation(animation);
                            ImageUploadCallBack sfzCallBack = new ImageUploadCallBack(sfz, mSfzLoading);
                            params.addBodyParameter("imgtype", "4");
                            params.addBodyParameter("key", UserHelper.getInstance().getUid());
                            RequestManager.imageUpload(params, sfzCallBack);
                            break;
                        case UserConstant.LICENCE_REQUEST_CODE:
                            jszBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            jsz.setImageBitmap(jszBitmap);
                            jsz.setTag("upload");
                            licencePathOnSaveValue = result;
                            jszLoading.setVisibility(View.VISIBLE);
                            jszLoading.setAnimation(animation);
                            ImageUploadCallBack licenceCallBack = new ImageUploadCallBack(jsz, jszLoading);
                            params.addBodyParameter("imgtype", "10");
                            params.addBodyParameter("key", UserHelper.getInstance().getUid());
                            RequestManager.imageUpload(params, licenceCallBack);
                            break;
                        case UserConstant.JOBCARD_REQUEST_CODE:
                            cyzgzBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            cyzgz.setImageBitmap(cyzgzBitmap);
                            cyzgz.setTag("upload");
                            jobcardPathOnSaveValue = result;
                            cyzgzLoading.setVisibility(View.VISIBLE);
                            cyzgzLoading.setAnimation(animation);
                            ImageUploadCallBack jobcardCallBack = new ImageUploadCallBack(cyzgz, cyzgzLoading);
                            params.addBodyParameter("imgtype", "9");
                            params.addBodyParameter("key", UserHelper.getInstance().getUid());
                            RequestManager.imageUpload(params, jobcardCallBack);
                            break;
                        case UserConstant.BUS_REQUEST_CODE:
                            ctxBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            mBusIcon.setImageBitmap(ctxBitmap);
                            mBusIcon.setTag("upload");
                            busPathOnSaveValue = result;
                            busLoading.setVisibility(View.VISIBLE);
                            busLoading.setAnimation(animation);
                            ImageUploadCallBack busCallBack = new ImageUploadCallBack(mBusIcon, busLoading);
                            params.addBodyParameter("imgtype", "3");
                            params.addBodyParameter("key", UserHelper.getInstance().getCid());
                            RequestManager.imageUpload(params, busCallBack);
                            break;
                        case UserConstant.INSURANCE_REQUEST_CODE:
                            bxzBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            mInsurance.setImageBitmap(bxzBitmap);
                            mInsurance.setTag("upload");
                            insurancePathOnSaveValue = result;
                            insuranceLoading.setVisibility(View.VISIBLE);
                            insuranceLoading.setAnimation(animation);
                            ImageUploadCallBack insuranceCallBack = new ImageUploadCallBack(mInsurance, insuranceLoading);
                            params.addBodyParameter("imgtype", "7");
                            params.addBodyParameter("key", UserHelper.getInstance().getCid());
                            RequestManager.imageUpload(params, insuranceCallBack);
                            break;
                        case UserConstant.XSZ_REQUEST_CODE:
                            xszBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            xsz.setImageBitmap(xszBitmap);
                            xsz.setTag("upload");
                            routePathOnSaveValue = result;
                            xszLoading.setVisibility(View.VISIBLE);
                            xszLoading.setAnimation(animation);
                            ImageUploadCallBack routetransportCallBack = new ImageUploadCallBack(xsz, xszLoading);
                            params.addBodyParameter("imgtype", "8");
                            params.addBodyParameter("key", UserHelper.getInstance().getCid());
                            RequestManager.imageUpload(params, routetransportCallBack);
                            break;
                        case UserConstant.JYXKZ_CODE:
                            jyxkzBitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                            jyxkz.setImageBitmap(jyxkzBitmap);
                            jyxkz.setTag("upload");
                            jyxkzLoading.setVisibility(View.VISIBLE);
                            jyxkzLoading.setAnimation(animation);
                            ImageUploadCallBack jyxkzCallBack = new ImageUploadCallBack(jyxkz, jyxkzLoading);
                            params.addBodyParameter("imgtype", "11");
                            params.addBodyParameter("key", UserHelper.getInstance().getCid());
                            RequestManager.imageUpload(params, jyxkzCallBack);
                            break;
                    }
                }
                break;
            case 3: // 车辆品牌
                String mode = data.getStringExtra("mode");
                if (!TextUtils.isEmpty(mode)) {
                    mBusBrand.setText(mode);
                }
                break;
            case 4: // 所属地区
                String city = data.getStringExtra("city");
                mBusCity.setText(city);
                break;
        }
    }

    private void initView() {
        mLicencePostfix.addTextChangedListener(myTextWatcher);
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在努力加载中...");
        dialog.setCancelable(true);
    }


    private void doPickPhotoAction(int requestCode, String extraValue) {
        Intent intent = new Intent(this, PickPhotoActivity.class);
        intent.putExtra("title", "获取图片");
        intent.putExtra("crop", true);
        intent.putExtra("option", PhotoSelectOptions.DEFALUT);
        intent.putExtra(PickPhotoActivity.FILE_NAME, extraValue);
        startActivityForResult(intent, requestCode);
    }

    private void hideInputMethod() {
        InputMethodManager mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mNextBtn.getWindowToken(), 0);
    }

    private void smoothScrollToView(View v) {
        if (mLoginState == 1) {
            if (mScrollView1 != null && v != null) {
                mScrollView1.smoothScrollTo(0, v.getTop());
            }
        } else if (mLoginState == 2) {
            if (mScrollView2 != null && v != null) {
                mScrollView2.smoothScrollTo(0, v.getTop());
            }
        }
    }

    private boolean verifyDriver() {
        if (TextUtils.isEmpty(mNameEditText.getText().toString()) || TextUtils.isEmpty(mNameEditText.getText().toString().trim())) {
            ToastHelper.showToast("请填写姓名");
            mNameEditText.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mCompanyEditText.getText().toString()) || TextUtils.isEmpty(mCompanyEditText.getText().toString().trim())) {
            ToastHelper.showToast("请填写单位名称");
            mCompanyEditText.requestFocus();
            return false;
        }
        if (sfz.getTag() == null) {
            ToastHelper.showToast("请选择身份证照片");
            return false;
        }
        if ("upload".equals(sfz.getTag().toString())) {
            ToastHelper.showToast("身份证照片上传中");
            return false;
        }
        if ("failure".equals(sfz.getTag().toString())) {
            ToastHelper.showToast("身份证照片上传失败，请重新选择照片");
            return false;
        }
        if (jsz.getTag() == null) { // 驾驶证 必填
            ToastHelper.showToast("请选择驾驶证");
            return false;
        }
        if ("upload".equals(jsz.getTag().toString())) {
            ToastHelper.showToast("驾驶证上传中..");
            return false;
        }
        if ("failure".equals(jsz.getTag().toString())) {
            ToastHelper.showToast("驾驶证上传失败，请重新选择图片");
            return false;
        }
        if (mHead.getTag() != null) {
            if ("upload".equals(mHead.getTag().toString())) {
                ToastHelper.showToast("头像上传中..");
                return false;
            }
            if ("failure".equals(mHead.getTag().toString())) {
                ToastHelper.showToast("头像上传失败，请重新选择照片");
                return false;
            }
        }
        if (cyzgz.getTag() != null) {
            if ("upload".equals(cyzgz.getTag().toString())) {
                ToastHelper.showToast("从业资格证上传中..");
                return false;
            }
            if ("failure".equals(cyzgz.getTag().toString())) {
                ToastHelper.showToast("从业资格证上传失败，请重新选择照片");
                return false;
            }
        }
        return true;
    }

    private boolean verifyBus() {
        if (TextUtils.isEmpty(mLicencePrefix.getText().toString())) {
            ToastHelper.showToast("请选择车牌号城市");
            mLicencePrefix.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mLicencePostfix.getText().toString())) {
            ToastHelper.showToast("请填写车牌号");
            mLicencePostfix.requestFocus();
            return false;
        }
        int a1 = mLicencePrefix.getText().length();
        int a2 = mLicencePrefix.getText().length();
        if ((mLicencePrefix.getText().length() + mLicencePostfix.getText().length()) != 7) {
            ToastHelper.showToast("请正确填写车牌号");
            return false;
        }
        if (TextUtils.isEmpty(mBusCity.getText().toString())) {
            ToastHelper.showToast("请选择所属地区");
            return false;
        }
        if (TextUtils.isEmpty(mBusBrand.getText().toString())) {
            ToastHelper.showToast("请选择品牌");
            return false;
        }
        if (TextUtils.isEmpty(mBusType.getText().toString())) {
            ToastHelper.showToast("请选择购车时间");
            return false;
        }
        if (TextUtils.isEmpty(mBusSeatNum.getText().toString())) {
            ToastHelper.showToast("请填写座位数");
            mBusSeatNum.requestFocus();
            return false;
        }
        if (mInsurance.getTag() == null) { // 保险 必填
            ToastHelper.showToast("请选择保险照片");
            return false;
        }
        if ("upload".equals(mInsurance.getTag().toString())) {
            ToastHelper.showToast("保险证上传中..");
            return false;
        }
        if ("faliure".equals(mInsurance.getTag().toString())) {
            ToastHelper.showToast("保险照片上传失败，请重新选择照片");
            return false;
        }
        if (xsz.getTag() == null) {
            ToastHelper.showToast("请选择行驶证照片");
            return false;
        }
        if ("upload".equals(xsz.getTag().toString())) {
            ToastHelper.showToast("行驶证证上传中..");
            return false;
        }
        if ("failure".equals(xsz.getTag().toString())) {
            ToastHelper.showToast("行驶证上传失败，请重新选择照片");
            return false;
        }

        if (mBusIcon.getTag() != null) {
            if ("upload".equals(mBusIcon.getTag().toString())) {
                ToastHelper.showToast("车辆照片上传中..");
                return false;
            }
            if ("failure".equals(mBusIcon.getTag().toString())) {
                ToastHelper.showToast("车辆照片上传失败，请重新选择照片");
                return false;
            }
        }
        if (xsz.getTag() != null) {
            if ("upload".equals(xsz.getTag().toString())) {
                ToastHelper.showToast("行驶证上传中..");
                return false;
            }
            if ("failure".equals(xsz.getTag().toString())) {
                ToastHelper.showToast("行驶证照片上传失败，请重新选择照片");
                return false;
            }
        }
        return true;
    }

    class SignUpCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            JSONObject jo = XString.getJSONObject(responseInfo.result);
            boolean status = XString.getBoolean(jo, "status");
            JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
            if (status) {
                app.toast("注册成功");
                UserHelper.getInstance().setName(mNameEditText.getText().toString());
                UserHelper.getInstance().setSex(sex);
                UserHelper.getInstance().setCompany(mCompanyEditText.getText().toString());
                UserHelper.getInstance().setBackupPhone(mBackupPhone.getText().toString());
                UserHelper.getInstance().setBusNumber(mLicencePostfix.getText().toString());
                UserHelper.getInstance().setBusCity(mBusCity.getText().toString());
                UserHelper.getInstance().setBusBrand(mBusBrand.getText().toString());
                UserHelper.getInstance().setSeat(mBusSeatNum.getText().toString());
                //UserHelper.getInstance().setBusType(mBusType.getText().toString());
                // TODO 检查
                new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this)
                        .setTitle("提示")
                        .setCancelable(false)
                        .setNegativeButton("前去首页", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setPositiveButton("上传更多", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignUpActivity.this, NoBackActivity.class);
                                intent.putExtra("fragmentName", CarPhotoFragment.class.getName());
                                startActivity(intent);
                                finish();
                            }
                        }).create().show();

            } else {
                app.toast(XString.getStr(data, "msg"));
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            app.toast("您的网络不给力");
        }
    }

    private void showDialog() {
        final Calendar c;
        if (mBusType.getTag() == null) {
            c = Calendar.getInstance();
            mBusType.setTag(c);
        } else {
            c = (Calendar) mBusType.getTag();
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                mBusType.setText(DateUtils.getTyyyyMMdd(c.getTimeInMillis()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        String[] cartype = {"普通型", "舒适型", "豪华型", "取消"};
//        builder.setItems(cartype, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        mBusType.setText("普通型");
//                        mBusType.setTag("1");
//                        break;
//                    case 1:
//                        mBusType.setText("舒适型");
//                        mBusType.setTag("2");
//                        break;
//                    case 2:
//                        mBusType.setText("豪华型");
//                        mBusType.setTag("3");
//                        break;
//                }
//            }
//        });
//        builder.create().show();
    }

    class ImageUploadCallBack extends RequestCallBack<String> {
        ImageView image;
        ImageView loading;

        public ImageUploadCallBack(ImageView image, ImageView loading) {
            this.image = image;
            this.loading = loading;
        }

        @Override
        public void onSuccess(ResponseInfo responseInfo) {
            ToastHelper.showToast("图片上传成功");
            image.setTag("success");
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            ToastHelper.showToast("图片上传失败，请重新选择照片");
            image.setTag("failure");
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
        }
    }

    TextWatcher myTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String busNumber = mLicencePostfix.getText().toString();
            String uppercase = busNumber.toUpperCase();
            if (!uppercase.equals(busNumber)) {
                mLicencePostfix.setText(uppercase);
                mLicencePostfix.setSelection(uppercase.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[^A-Z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
