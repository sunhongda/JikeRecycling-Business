package com.buslink.busjie.driver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.viewholder.HeadHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/8.
 */
public class HeadAdapter extends RecyclerView.Adapter<HeadHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private ArrayList<String> mDatas = new ArrayList<>();
    private View mHeaderView;
    private View itemView;
    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }
    public void HeadAdapter (View itemView){
        this.itemView=itemView;

    }
    public void HeadAdapter (HeadHolder headHolder){


    }

    public View getHeaderView() {
        return mHeaderView;
    }
    public void addDatas(ArrayList<String> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) return TYPE_NORMAL;
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }
    @Override
    public  HeadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) return new HeadHolder(itemView,mHeaderView);
       // View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_detail, parent, false);
        final  HeadHolder holder = new HeadHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ad_detail, parent, false),mHeaderView);
        holder.setTag(R.id.tv_order_nb, R.id.iv_passenger, R.id.tv_name,
                R.id.tv_phone_num, R.id.tv_booking,
                R.id.tv_pay_state, R.id.tv_pay_price,
                R.id.bt_is_in);

        return holder;
        //return new Holder(layout);
    }



    @Override
    public void onBindViewHolder(HeadHolder holder, int position) {
//        if(getItemViewType(position) == TYPE_HEADER) return;
//
//        final int pos = getRealPosition(holder);
//        final List data = mDatas.get(pos);
//        if(holder instanceof HeadHolder) {
//           // ((Holder) viewHolder).text.setText(data);
//                    holder.setIndex(position);
//        final JSONObject jo = data.get(position);
//        holder.getTextView(R.id.tv_order_nb).setText("订单号：" + XString.getStr(jo, JsonName.ORDERNO));
//
//        Picasso.with()
//                .load(Net.IMGURL + XString.getStr(jo, JsonName.IMG))
//                .placeholder(R.mipmap.home_wo_default)
//                .error(R.mipmap.home_wo_default)
//                .transform(new CircleTransform())
//                .into(holder.<ImageView>getTag(R.id.iv_passenger));
//        // holder.getImageView(R.id.iv_passenger).setText();
//        holder.getTextView(R.id.tv_name).setText(XString.getStr(jo, JsonName.NAME));
//        holder.getTextView(R.id.tv_phone_num).setText("电话:" + XString.getStr(jo, JsonName.PHONE));
//        holder.getTextView(R.id.tv_booking).setText("订座：" + XString.getStr(jo, JsonName.SEATTOTAL) + "个");
//        //订单状态1已支付;2. 订票失败（退款到零钱）3订单已退款（乘客申请退款）
//        String orderstate = null;
//        switch (XString.getInt(jo, JsonName.ORDERSTATE)) {
//            case 1:
//                orderstate = "已支付";
//                break;
//            case 2:
//                orderstate = "订票失败";
//                break;
//            case 3:
//                orderstate = "3订单已退款";
//                break;
//
//        }
//        holder.getTextView(R.id.tv_pay_state).setText(orderstate);
//
//
//        holder.getTextView(R.id.tv_pay_price).setText(XString.getStr(jo, JsonName.QUOTED) + "元");//支付金额
//        //司机检票0 未检票（乘客未到）；1已检票（乘客已到）; 默认0
//
//
////                orderrequirestate  订单状态 1订单正常;2. 订票失败（退款到零钱）3订单已退款（乘客申请退款）  ;默认1
////                        orderrequirestate=1 正常订单才有检票环节;
//        if (XString.getInt(jo, JsonName.ORDERREQUIRESTATE) == 1) {
//            String isin = "0";
//            switch (XString.getInt(jo, JsonName.ISARRIVE)) {
//                case 0:
//                    isin = "未检票";
//                    break;
//                case 1:
//                    isin = "已检票";
//                    break;
//                default:
//                    isin = "未检票";
//                    break;
//            }
//            holder.getButton(R.id.bt_is_in).setVisibility(View.VISIBLE);
//            holder.getButton(R.id.bt_is_in).setText(isin);
//            holder.getButton(R.id.bt_is_in).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                  //  checkIsArrive(jo,holder,XString.getInt(jo, JsonName.ISARRIVE));
//
//                }
//            });
//
//
//        }
////
//
//
//
//
//
//
//
//            if(mListener == null) return;
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                //    mListener.onItemClick(pos, data);
//                }
//            });
//        }





    }
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }
    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }


//    class Holder extends RecyclerView.ViewHolder {
//        private View v;
//        private int index;
//        public Holder(View itemView) {
//            super(itemView);
//            if(itemView == mHeaderView)
//                return;
//         //   text = (TextView) itemView.findViewById(R.id.text);
//        }
//
//        public void setTag(int... ids) {
//            if (v == null) {
//                return;
//            }
//            for (int id : ids) {
//                v.setTag(id, v.findViewById(id));
//            }
//        }
//
//        public View getView(int id) {
//            return (View) v.getTag(id);
//        }
//
//        public TextView getTextView(int id) {
//            return (TextView) v.getTag(id);
//        }
//        public Button getButton(int id){
//            return (Button) v.getTag(id);
//        }
//
//        public LinearLayout getLinerLayout(int id){
//            return (LinearLayout)v.getTag(id);
//        }
//
//        public ImageView getImageView(int id) {
//            return (ImageView) v.getTag(id);
//        }
//
//        public int getIndex() {
//            return index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        public <T> T getTag(int id){
//            return (T)v.getTag(id);
//        }
//
//
//    }
    interface OnItemClickListener {
        void onItemClick(int position, String data);
    }
}
