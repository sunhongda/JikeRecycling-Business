package com.buslink.busjie.driver.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.entity.MyEvent;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import de.greenrobot.event.EventBus;

public class ChooseBankFragment extends LevelTwoFragment {

    private BankAdapter adapter;

    @ViewInject(R.id.bank_list)
    ListView mBankList;

    @OnItemClick(R.id.bank_list)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String bankName = adapter.getItem(position);
        MyEvent e = new MyEvent(EventName.ChooseBank);
        e.setData(bankName);
        e.putExtra("btype", position);
        EventBus.getDefault().post(e);
        mActivity.finish();
    }

    @Override
    public String getTitle() {
        return "选择银行卡";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_choosebank;
    }

    @Override
    protected void initView() {
        Resources res = getResources();
        String[] bankNames = res.getStringArray(R.array.string_bank_name);
        TypedArray bankIcons = res.obtainTypedArray(R.array.bank_icons);
        adapter = new BankAdapter(mActivity, bankNames, bankIcons);
        mBankList.setAdapter(adapter);
        //bankIcons.recycle();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    class BankAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final String[] bankNames;
        private final TypedArray bankIcons;

        public BankAdapter(Context context, String[] bankNames, TypedArray bankIcons) {
            super(context, -1, bankNames);
            this.context = context;
            this.bankNames = bankNames;
            this.bankIcons = bankIcons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bank_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(bankNames[position]);
            imageView.setImageDrawable(bankIcons.getDrawable(position));
            return rowView;
        }
    }
}
