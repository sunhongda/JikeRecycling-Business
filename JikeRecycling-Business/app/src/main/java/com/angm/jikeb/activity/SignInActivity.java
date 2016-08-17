package com.angm.jikeb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BackActivity;
import com.angm.jikeb.base.BaseActivity;
import com.angm.jikeb.fragment.SignUpFragment;
import com.angm.jikeb.util.Alert;
import com.angm.jikeb.util.DesUtils;
import com.angm.jikeb.util.MobileUtils;
import com.angm.jikeb.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;


//登录
public class SignInActivity extends BaseActivity {


    @Bind(R.id.signin_chechkbox)
    CheckBox signinChechkbox;
    @Bind(R.id.signin_start_button)
    Button signinStartButton;
    @Bind(R.id.signin_mobile_edittext)
    EditText signinMobileEdittext;
    @Bind(R.id.signin_pwd_edittext)
    EditText signinPwdEdittext;
    @Bind(R.id.tv_toolbar_center)
    TextView tvToolbarCenter;
    @Bind(R.id.signin_test_button)
    Button signinTestButton;
    @Bind(R.id.tv_forget_sign_up)
    TextView tvForgetSignUp;
    private String encode;
    private Map<String, Object> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public int getLayout() {
        return R.layout.sign_in;
    }

    @Override
    public void initView() {
        tvToolbarCenter.setText("登陆");
        tvForgetSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFragment(BackActivity.class, SignUpFragment.class);
            }
        });
        signinStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
        signinTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void initData() {
    }

    public void validation() {
        String mobile = signinMobileEdittext.getText().toString().trim();/*手机号*/
        String pwd = signinPwdEdittext.getText().toString().trim();/*密码*/
        boolean mobileNO = MobileUtils.isMobileNO(mobile);/*手机号正则表达式*/
        if (mobileNO) {
            if (TextUtils.isEmpty(pwd)) {
                Toast.makeText(SignInActivity.this, R.string.sign_in_pwd, Toast.LENGTH_SHORT).show();
            } else if (pwd.length() < 6 || pwd.length() > 20) {
                Toast.makeText(SignInActivity.this, R.string.sign_in_pwd_count, Toast.LENGTH_SHORT).show();
            } else if (!signinChechkbox.isChecked()) {
                Toast.makeText(SignInActivity.this, R.string.sign_in_check, Toast.LENGTH_SHORT).show();
            } else {
                Alert.showDialog(SignInActivity.this);
                String[] strPath = new String[]{mobile};
                Log.d("zzz", "strPath+" + SignUtil.getSign(strPath));
                try {
                    encode = DesUtils.encode(mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                boolean networkAvailable = MobileUtils.isNetworkAvailable(SignInActivity.this);
                if (networkAvailable) {
                    map.put("sign", SignUtil.getSign(strPath));
                    map.put("mobile", encode);
//                OkHttpUtils.getInstance(new OkHttpUtils.HttpCallBackListener() {
//                    @Override
//                    public void response(String result) {
//                        android.util.Log.d("zzz", "result+" + result);
                    Alert.mProgressDialog.dismiss();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
//                    }
//                }).POST_DATA(path, map);
                } else {
                    Alert.mProgressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, R.string.sign_in_network, Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            Toast.makeText(SignInActivity.this, R.string.sign_in_mobile_format, Toast.LENGTH_SHORT).show();

        }
    }
}



















