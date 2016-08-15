package com.angm.jikeb.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.angm.jikeb.R;
import com.angm.jikeb.constant.NetWorkConstant;
import com.angm.jikeb.util.Alert;
import com.angm.jikeb.util.DesUtils;
import com.angm.jikeb.util.MobileUtils;
import com.angm.jikeb.util.OkHttpUtils;
import com.angm.jikeb.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;


//注册页
public class SignUpFragment extends LevelTwoFragment {

    @Bind(R.id.sign_up_verify_button)
    Button signUpVerifyButton;
    @Bind(R.id.sign_up_button)
    Button signUpButton;
    @Bind(R.id.sign_up_mobile_edittext)
    EditText signUpMobileEdittext;
    @Bind(R.id.sign_up_captcha_edittext)
    EditText signUpCaptchaEdittext;
    @Bind(R.id.ed_setpwd)
    EditText edSetpwd;
    @Bind(R.id.ed_yespwd)
    EditText edYespwd;
    @Bind(R.id.sign_up_chechkbox)
    CheckBox signUpChechkbox;

    private String encodeMobile;
    private String encodeVerify;
    private String encodePwd1;
    private String encodePwd2;
    private String mobile;
    private String verify;
    private String pwd1;
    private String pwd2;
    private boolean stopThread = false;
    private Map<String, Object> mapValue = new HashMap<>();
    private Map<String, Object> mapVerCode = new HashMap<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String verCode = (String) msg.obj;
            if (msg.arg1 == 0) {
                signUpVerifyButton.setText("发送验证码");
                signUpVerifyButton.setEnabled(true);
            } else {
                signUpVerifyButton.setEnabled(false);
                signUpVerifyButton.setText("重新发送" + msg.arg1);
            }
            if (msg.what == 1) {
                if (verCode != null) {
                    signUpCaptchaEdittext.setText(verCode);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "注册";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_sign_up_bus;
    }

    @Override
    public void initView() {
        signUpVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVerCode();

            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }

    public void getVerCode() {
        encryPtion();
        if (!TextUtils.isEmpty(mobile)) {
            boolean mobileNO = MobileUtils.isMobileNO(mobile);
            if (mobileNO) {
                String[] signCode = new String[]{mobile};
                mapVerCode.put("sign", SignUtil.getSign(signCode));
                mapVerCode.put("mobile", encodeMobile);
                boolean networkAvailable = MobileUtils.isNetworkAvailable(getActivity());
                if (networkAvailable) {
                    OkHttpUtils.getInstance(new OkHttpUtils.HttpCallBackListener() {
                        @Override
                        public void response(String result) {
                            Log.d("zzz", "code" + result);
                            ShowTime();
                            Looper.prepare();
                            Toast.makeText(getActivity(), R.string.sign_send_code_success, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }).POST_DATA(NetWorkConstant.GET_VERCODE, mapVerCode);
                } else {
                    Toast.makeText(getActivity(), R.string.sign_in_network, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.sign_in_mobile_format, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.sign_in_mobile, Toast.LENGTH_SHORT).show();
        }
    }

    public void encryPtion() {
        mobile = signUpMobileEdittext.getText().toString().trim();
        verify = signUpCaptchaEdittext.getText().toString().trim();
        pwd1 = edSetpwd.getText().toString().trim();
        pwd2 = edYespwd.getText().toString().trim();
        try {
            encodeMobile = DesUtils.encode(mobile);
            encodeVerify = DesUtils.encode(verify);
            encodePwd1 = DesUtils.encode(pwd1);
            encodePwd2 = DesUtils.encode(pwd2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validation() {
        encryPtion();
        boolean mobileNO = MobileUtils.isMobileNO(mobile);
        if (mobileNO) {
            if (TextUtils.isEmpty(verify)) {
                Toast.makeText(getActivity(), R.string.sign_in_verify, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pwd1)) {
                Toast.makeText(getActivity(), R.string.sign_in_pwd, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pwd2)) {
                Toast.makeText(getActivity(), R.string.sign_in_pwd, Toast.LENGTH_SHORT).show();
            } else if (!pwd1.equals(pwd2)) {
                Toast.makeText(getActivity(), R.string.sign_in_pwd_pwd2, Toast.LENGTH_SHORT).show();
            } else if (verify.length() < 4) {
                Toast.makeText(getActivity(), R.string.sign_in_verify_count, Toast.LENGTH_SHORT).show();
            } else if (!signUpChechkbox.isChecked()) {
                Toast.makeText(getActivity(), R.string.sign_in_check, Toast.LENGTH_SHORT).show();
            } else {
                Alert.showDialog(getActivity());
                boolean networkAvailable = MobileUtils.isNetworkAvailable(getActivity());
                if (networkAvailable) {
                    String[] signRegister = new String[]{mobile, verify, pwd1, pwd2, "2"};
                    mapValue.put("mobile", encodeMobile);
                    mapValue.put("verCode", encodeVerify);
                    mapValue.put("password", encodePwd1);
                    mapValue.put("password2", encodePwd2);
                    mapValue.put("userType", "2");
                    mapVerCode.put("sign", SignUtil.getSign(signRegister));
                    OkHttpUtils.getInstance(new OkHttpUtils.HttpCallBackListener() {
                        @Override
                        public void response(String result) {
                            Log.d("zzz", "注册成功没？=" + result);
                            Alert.mProgressDialog.dismiss();
                        }
                    }).POST_DATA(NetWorkConstant.REGISTER, mapValue);
                } else {
                    Alert.mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.sign_in_network, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), R.string.sign_in_mobile_format, Toast.LENGTH_SHORT).show();
        }
    }

    /*倒计时方法*/
    public void ShowTime() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 30; i >= 0; i--) {
                    SystemClock.sleep(1000);
                    Message msg = Message.obtain();
                    msg.arg1 = i;
                    if (!stopThread) {
                        handler.sendMessage(msg);
                    } else {
                        i = 0;
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread = true;
    }
}
