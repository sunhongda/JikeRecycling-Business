package com.buslink.busjie.driver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.buslink.busjie.driver.util.DensityUtils;

/**
 * Created by Administrator on 2015/9/26.
 */
public class HomeView extends View {

    private Paint mPaint = new Paint();// 渐变色环画笔，抗锯齿
    private final int[] mColors = new int[] { 0xffff0000, 0xffffff00, 0xff00ff00,
            0xff00ffff,0xff0000ff,0xffff0000 };// 渐变色环颜色

    private int width ;
    private int height ;

    private int CENTER_X;//中心点x
    private int CENTER_Y;//中心点x

    private int round;
    private Paint paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Shader sweep;


    public HomeView(Context context) {
        super(context);
        init();
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f - 10;
        canvas.save();
        canvas.translate(CENTER_X, CENTER_X);// 移动中心
        canvas.rotate(135);
        mPaint.setARGB(255, 150, 150, 150);
        mPaint.setShader(null);
        canvas.drawArc(new RectF(-r, -r, r, r), 0, 270, false, mPaint);
        mPaint.setShader(sweep);
        Log.d("round",""+ round);
        canvas.drawArc(new RectF(-r, -r, r, r), 0, round, false, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = width;
        CENTER_X = CENTER_Y = width / 2 ;
        Log.d("onMeasure","width:"+width+"height:"+height);
        setMeasuredDimension(width, height);
    }

    private void init() {
        sweep = new SweepGradient(0, 0, mColors, null);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DensityUtils.dip2px(getContext(), 12));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        paintBg.setColor(Color.WHITE);
        paintCircle.setColor(Color.WHITE);
    }
    public void setRound(int r){
        this.round=r;
    }
    public int getRound(){
        return round;
    }
}
