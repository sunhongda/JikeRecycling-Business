package com.buslink.busjie.driver.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.DensityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/10/8.
 */
public class TimeSelectDialog extends Dialog implements View.OnClickListener {
    Context context;
    SelectTimeListener listener;
    private TextView tvTile;
    private TextView tvLeft, tvRight;
    private PickerView pvD, pvH, pvM;
    private Calendar c,c1;
    String d,h,m;
    List<String> dString, hString, mString;

    public TimeSelectDialog(Context context, Calendar c) {
        super(context);
        this.context = context;
        if(c==null){
            c=Calendar.getInstance();
        }
        this.c=c;
        c1=(Calendar)c.clone();
        initView();
        initListener();
        initData();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_select_time);
        tvTile = (TextView) findViewById(R.id.tv_title);
        tvLeft = (TextView) findViewById(R.id.left);
        tvRight = (TextView) findViewById(R.id.right);
        pvD = (PickerView) findViewById(R.id.pv);
        pvH = (PickerView) findViewById(R.id.pv_1);
        pvM = (PickerView) findViewById(R.id.pv_2);
        pvD.setTextSize(DensityUtils.dip2px(context, 20));
        pvH.setTextSize(DensityUtils.dip2px(context, 20));
        pvM.setTextSize(DensityUtils.dip2px(context, 20));
        setCancelable(false);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.down2up);
    }

    public void initListener() {
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        pvD.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                c=(Calendar)c1.clone();
                c.add(Calendar.DAY_OF_YEAR, dString.indexOf(text));
            }
        });
        pvH.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                c.set(Calendar.HOUR_OF_DAY, getNumbers(text));
                c1.set(Calendar.HOUR_OF_DAY, getNumbers(text));
            }
        });
        pvM.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                c.set(Calendar.MINUTE, getNumbers(text));
                c1.set(Calendar.MINUTE, getNumbers(text));
            }
        });
    }

    public int getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return Integer.valueOf(matcher.group(0));
        }
        return 0;
    }
    public void initData() {
        dString = getDString();
        hString = getHString();
        mString = getMString();
        pvD.setData(dString);
        pvH.setData(hString);
        pvM.setData(mString);
    }

    public interface SelectTimeListener {
        void onSelect(String date);
    }

    private List<String> getDString() {
        List<String> list = new ArrayList<>();
        Long time = c.getTimeInMillis();
        //控制天数的
        for (int i = 0; i < 90; i++) {
            list.add(DateUtils.getYer(time + i * 1000 * 60 * 60 * 24l));
        }
        return list;
    }

    private List<String> getHString() {
        List<String> list = new ArrayList<>();
        Long time = c.getTimeInMillis();
        for (int i = 0; i < 24; i++) {
            list.add(DateUtils.getHh(time + i * 1000 * 60 * 60));
        }
        return list;
    }

    private List<String> getMString() {
        List<String> list = new ArrayList<>();
        Long time = c.getTimeInMillis();
        for (int i = 0; i < 60; i++) {
            list.add(DateUtils.getMm(time + i * 1000 * 60));
        }
        return list;
    }

    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                this.dismiss();
                break;
            case R.id.right:
                listener.onSelect(DateUtils.getSimpleFormatTime(c.getTimeInMillis()));
                this.dismiss();
                break;
        }
    }

    public Calendar getC() {
        return c;
    }

    public void setC(Calendar c) {
        this.c = c;
    }

    public TimeSelectDialog setOnSelectTimeListener(SelectTimeListener listener) {
        this.listener = listener;
        return this;
    }
}
