package com.angm.jikeb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.angm.jikeb.R;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/18.
 */
public class RewardAdapter extends BaseAdapter {
    private Context context;

    public RewardAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.fragment_message_item, null);
        ViewHolder holder = new ViewHolder(view);
        holder.fragmentMessageLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点也没用,我根本没做", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.fragment_message_tv_title)
        TextView fragmentMessageTvTitle;
        @Bind(R.id.fragment_message_tv_content)
        TextView fragmentMessageTvContent;
        @Bind(R.id.fragment_message_ll)
        AutoRelativeLayout fragmentMessageLl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
