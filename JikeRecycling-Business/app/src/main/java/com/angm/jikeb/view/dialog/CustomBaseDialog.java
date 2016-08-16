package com.angm.jikeb.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.view.dialog.base.BaseDialog;
import com.angm.jikeb.view.dialog.base.CornerUtils;
import com.angm.jikeb.view.dialog.custom.Swing;
import com.angm.jikeb.view.dialog.custom.ViewFindUtils;


public class CustomBaseDialog extends BaseDialog<CustomBaseDialog> {
    private TextView tv_cancel;
    private TextView tv_sure;
    private TextView dialog_msg;
    private LinearLayout chbeckbox_sel;
    private CheckBox chbeckbox;

    public CustomBaseDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.85f);
        showAnim(new Swing());

        // dismissAnim(this, new ZoomOutExit());
        View inflate = View.inflate(context, R.layout.dialog_custom_base, null);
        tv_cancel = ViewFindUtils.find(inflate, R.id.tv_cancel);
        tv_sure = ViewFindUtils.find(inflate, R.id.tv_sure);
        dialog_msg = ViewFindUtils.find(inflate, R.id.dialog_msg);
        chbeckbox_sel = ViewFindUtils.find(inflate, R.id.chbeckbox_sel);
        chbeckbox = ViewFindUtils.find(inflate, R.id.chbeckbox);
        inflate.setBackgroundDrawable(
                CornerUtils.cornerDrawable(Color.parseColor("#ffffff"), dp2px(5)));

        return inflate;
    }
    @Override
    public void setUiBeforShow() {
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        tv_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
    }

    public void setText(String text){
        dialog_msg.setText(text);
    }
    //复选框显示隐藏方法
    public void chbeckoxstae(int state){
        chbeckbox_sel.setVisibility(state);
    }
    //复选框状态方法
    public Boolean chbeckox(){
        return  chbeckbox.isChecked();
    }

    //隐藏取消按钮
    public void hideCancle(){
        if (tv_cancel!=null){
            tv_cancel.setVisibility(View.GONE);
        }
    }

    public void setListener(View.OnClickListener listener){
        tv_sure.setOnClickListener(listener);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    };

    public void setButton(String po,String ne){
        tv_sure.setText(po);
        tv_cancel.setText(ne);
    }
}
