package com.angm.jikeb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;
import com.angm.jikeb.util.Alert;
import com.angm.jikeb.util.EncryptUtil;
import com.angm.jikeb.util.OkHttpUtils;
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
    private String path = "http://115.28.242.49:8080/getVerCode.do";
    private Map<String, Object> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initToolbar();
    }

    @Override
    public int getLayout() {
        return R.layout.sign_in;
    }

    @Override
    public void initView() {

    }

    private void initToolbar() {
    }

    @Override
    public void initData() {

        signinStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = signinMobileEdittext.getText().toString().trim();/*手机号*/
                String pwd = signinPwdEdittext.getText().toString().trim();/*密码*/
                boolean mobileNO = isMobileNO(mobile);/*手机号正则表达式*/
                if (mobileNO) {
                    if (TextUtils.isEmpty(pwd)) {
                        Toast.makeText(SignInActivity.this, R.string.sign_in_pwd, Toast.LENGTH_SHORT).show();
                    } else {
                        if (pwd.length() < 6 || pwd.length() > 20) {
                            Toast.makeText(SignInActivity.this, R.string.sign_in_pwd_count, Toast.LENGTH_SHORT).show();
                        } else {
                            if (!signinChechkbox.isChecked()) {
                                Toast.makeText(SignInActivity.this, R.string.sign_in_check, Toast.LENGTH_SHORT).show();
                            } else {
                                Alert.showDialog(SignInActivity.this);
                                String encryMobile = EncryptUtil.getEncryptString(mobile);
                                /*签名*/
                                String[] strPath = new String[]{path};
                                String sign = SignUtil.getSign(strPath);
                                map.put("mobile", encryMobile);
                                OkHttpUtils.getInstance(new OkHttpUtils.HttpCallBackListener() {
                                    @Override
                                    public void response(String result) {
                                        android.util.Log.d("zzz", "result+" + result);
                                        Alert.mProgressDialog.dismiss();
                                        startActivity(new Intent(SignInActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }).POST_DATA(path, map);
                            }
                        }
                    }
                } else {
                    Toast.makeText(SignInActivity.this, R.string.sign_in_mobile_format, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
}




















