package com.buslink.busjie.driver.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.BankIcon;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.XString;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;

public class BankListAdapter extends RecyclerSwipeAdapter<BankListAdapter.SimpleViewHolder> {

    private Activity mActivity;
    private List<JSONObject> mDataset;
    private boolean clickable;

    public BankListAdapter(Activity context, List<JSONObject> objects) {
        this.mActivity = context;
        this.mDataset = objects;
        Resources res = mActivity.getResources();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final JSONObject item = mDataset.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        viewHolder.swipeLayout.getSurfaceView().setEnabled(true);
        if (clickable) {
            viewHolder.swipeLayout.setSwipeEnabled(false);
            viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyEvent e = new MyEvent(EventName.ChooseMyBank);
                    e.putExtra("item", item);
                    EventBus.getDefault().post(e);
                    mActivity.finish();
                }
            });
        }

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog(XString.getStr(item, "bid"), viewHolder, position);
            }
        });

        String bankname = XString.getStr(item, "bank");
        viewHolder.bankIcon.setImageResource(BankIcon.getBankIcon(bankname));
        viewHolder.bankName.setText(bankname);
        viewHolder.cardNumber.setText(XString.getStr(item, "account"));
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    public void setData(JSONArray array) {
        try {
            mDataset.clear();
            for (int i = 0; i < array.length(); i++) {
                mDataset.add(array.getJSONObject(i));
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView bankIcon;
        TextView bankName;
        TextView cardNumber;
        ImageView imageDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            bankIcon = (ImageView) itemView.findViewById(R.id.bank_icon);
            bankName = (TextView) itemView.findViewById(R.id.bank_name);
            cardNumber = (TextView) itemView.findViewById(R.id.item_card_number);
            imageDelete = (ImageView) itemView.findViewById(R.id.trash);
        }
    }

    private void deleteBank(final String bid, final SimpleViewHolder viewHolder, final int position) {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("bid", bid);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.DELETEBANK, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jo = new JSONObject((responseInfo.result));
                    if (XString.getBoolean(jo, "status")) {
                        mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                        mDataset.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mDataset.size());
                        mItemManger.closeAllItems();
                        MyEvent e = new MyEvent(EventName.DeleteBank);
                        EventBus.getDefault().post(e);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.e(s, e);
            }
        });
    }

    private void confirmDialog(final String bid, final SimpleViewHolder viewHolder, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage("解绑银行卡？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBank(bid, viewHolder, position);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public void setClickable() {
        clickable = true;
    }
}
