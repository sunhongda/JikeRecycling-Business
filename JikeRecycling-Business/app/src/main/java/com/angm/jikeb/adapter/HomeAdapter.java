package com.angm.jikeb.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.angm.jikeb.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/9.
 */
public class HomeAdapter extends BaseAdapter {
    private Context context;
    private String[] title;
    private int[] image;

    public HomeAdapter(Context context, String[] title, int[] image) {
        this.context = context;
        this.title = title;
        this.image = image;
    }

    @Override
    public int getCount() {
        return title.length;
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
        view = View.inflate(context, R.layout.fragment_vp_home_item, null);
        ViewHolder vh = new ViewHolder(view);
        vh.fragmentVpHomeImage.setImageResource(image[i]);
        vh.fragmentVpHomeTv.setText(title[i] + "");
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.fragment_vp_home_image)
        ImageView fragmentVpHomeImage;
        @Bind(R.id.fragment_vp_home_tv)
        TextView fragmentVpHomeTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
