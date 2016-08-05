package com.buslink.busjie.driver.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.DensityUtils;

/**
 * Created by Administrator on 2015/11/24.
 */
public class CarAgeView extends View implements View.OnTouchListener {

    private int where = 0;
    private int lineHeight;
    private Bitmap bt;
    private Paint paint;
    private float[] array;

    public CarAgeView(Context context) {
        super(context);
        initData();
    }

    public CarAgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CarAgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        bt = BitmapFactory.decodeResource(getResources(), R.mipmap.img_car);
        lineHeight = DensityUtils.dip2px(getContext(), 16);
        paint = new Paint();
        paint.setAntiAlias(true);
        array = new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 100, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specModeWid = MeasureSpec.getMode(widthMeasureSpec);
        int specSizeWid = MeasureSpec.getSize(widthMeasureSpec);
        int specModeHei = MeasureSpec.getMode(heightMeasureSpec);
        int specSizeHei = MeasureSpec.getSize(heightMeasureSpec);
        switch (specModeWid) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                specSizeWid = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        float x = (float) specSizeWid / bt.getWidth() / 8;
        switch (specModeHei) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                specSizeHei = (int) x * bt.getHeight() + lineHeight * 2;
                break;
        }
        setMeasuredDimension(specSizeWid, specSizeHei);
        Matrix matrix = new Matrix();
        matrix.postScale(x, x); //长和宽放大缩小的比例
        bt = Bitmap.createBitmap(bt, 0, 0, bt.getWidth(), bt.getHeight(), matrix, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw(canvas, where);
    }

    private void draw(Canvas canvas, int index) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        paint.setColor(getResources().getColor(R.color.green));
        paint.setStyle(Paint.Style.FILL);
        paint.setColorFilter(null);
        canvas.drawLine(0, lineHeight / 1.2f, width, lineHeight / 1.2f, paint);
        for (int i = 0; i < 4; i++) {
            paint.setColor(getResources().getColor(R.color.bg));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 16 + bt.getWidth() * i + width / 8 * i + bt.getWidth() / 2f, lineHeight / 1.2f, lineHeight / 2f, paint);
            paint.setColor(getResources().getColor(R.color.green));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width / 16 + bt.getWidth() * i + width / 8 * i + bt.getWidth() / 2f, lineHeight / 1.2f, lineHeight / 2f, paint);
            if (i == index) {
                ColorMatrix mColorMatrix = new ColorMatrix();   //新建颜色矩阵对象
                mColorMatrix.set(array);                        //设置颜色矩阵的值
                paint.setColorFilter(new ColorMatrixColorFilter(mColorMatrix)); //设置画笔颜色过滤器
                paint.setColor(getResources().getColor(R.color.green));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(width / 16 + bt.getWidth() * i + width / 8 * i + bt.getWidth() / 2f, lineHeight / 1.2f, lineHeight / 3f, paint);
                paint.setColor(getResources().getColor(R.color.green));
            } else {
                paint.setColorFilter(null);
                paint.setColor(getResources().getColor(R.color.text_thread));
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(DensityUtils.dip2px(getContext(), 11));
            canvas.drawText(getString(i), width / 16 + bt.getWidth() * i + width / 8 * i + bt.getWidth() / 2f, lineHeight + bt.getHeight() + DensityUtils.dip2px(getContext(), 4), paint);
            canvas.drawBitmap(bt, width / 16 + bt.getWidth() * i + width / 8 * i, lineHeight, paint);
        }
    }

    private String getString(int index) {
        switch (index) {
            case 0:
                return "不限";
            case 1:
                return "三年以下";
            case 2:
                return "3-5年";
            case 3:
                return "5年以上";
            default:
                return "不限";
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIndex(event.getX());
        return true;
    }

    private void setIndex(float x) {
        for (int i = 0; i < 4; i++) {
            if (where != i && bt.getWidth() * 2f * i <= x && bt.getWidth() * 2f * (i + 1) > x) {
                setWhere(i);
                break;
            }
        }
    }

    public int getWhere() {
        return where;
    }

    public void setWhere(int where) {
        this.where = where;
        postInvalidate();
    }
}
