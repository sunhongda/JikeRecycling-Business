package com.angm.jikeb.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.angm.jikeb.R;
import com.angm.jikeb.activity.MainActivity;
import com.angm.jikeb.base.BackActivity;
import com.angm.jikeb.fragment.MapFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/13.
 */
// RecycView 适配器
public class MyRecycAdapter extends RecyclerView.Adapter<MyRecycAdapter.MyViewHolder> {
    private Context context;
    private List<String> data;
    private MainActivity activity;

    public MyRecycAdapter(Context context, List<String> data, MainActivity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycview_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.recyclerItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                View back = View.inflate(context, R.layout.fragment_recy_item_alert, null);
                Button btn_select = (Button) back.findViewById(R.id.recy_item_select);
                Button btn_yes = (Button) back.findViewById(R.id.recy_item_yes);
                final AlertDialog ad = ab.create();
                ad.setView(back);
                ad.show();
                btn_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startFragment(BackActivity.class, MapFragment.class);
                        ad.dismiss();
                    }
                });
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.recycler_item_btn)
        Button recyclerItemBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}