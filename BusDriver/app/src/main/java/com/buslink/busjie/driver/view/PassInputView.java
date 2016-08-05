package com.buslink.busjie.driver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
/**
 * Created by Administrator on 2015/9/14.
 */
public class PassInputView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    private GestureDetectorCompat mDetector;
    private boolean isLive;
    private int mScreenWidth, mScreenHeight;
    private Paint paint;
    private int boxWidth;
    private int boxStrart, boxTop;
    private int boxLeftMarg;
    private int padding;
    private int index;
    private int cicleRe;
    private int stackWidth;
    private int btWidth;
    private int btTop;
    private int size;
    private String t1, t2;
    private int err;
    private int key;
    private int keyDwon;
    private SurfaceHolder holder;
    private int where;
    private InputListener listener;
    private boolean isOne;

    public PassInputView(Context context) {
        super(context);
        init();
    }

    public PassInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PassInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isLive = true;
        this.holder = holder;
        holder();
    }

    public void holder() {
        if (holder == null || !isLive) {
            return;
        }
        Canvas c = holder.lockCanvas();
        if (c != null) {
            myDraw(c);
        }
        holder.unlockCanvasAndPost(c);
        if ((err > 0 && err++ < 73)) {
            keyDwon=0;
            holder();
        }else if(keyDwon!=0){
            if(keyDwon>6){
                keyDwon=0;
            }else if(keyDwon!=0){
                keyDwon++;
            }
            holder();
        }
    }

    private void myDraw(Canvas c) {
        c.drawRGB(255, 255, 255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stackWidth);
        if (err > 0) {
            paint.setARGB(2233, 233, 0, 0);
            if (where == 0 && err < 73) {
                boxStrart -= err / 12 * 5;
                if (boxStrart <= 0) {
                    boxStrart = 0;
                    where = 1;
                }
            } else if (where == 1 && err < 73) {
                boxStrart += err / 12 * 5;
                if (boxStrart >= boxLeftMarg * 2) {
                    boxStrart = boxLeftMarg;
                    where = 0;
                }
            }
        } else {
            paint.setARGB(245, 186, 186, 186);

        }
        for (int i = 0; i < 6; i++) {
            c.drawRect(boxStrart + boxWidth * i, boxTop, boxStrart + boxWidth + boxWidth * i, boxTop + boxWidth, paint);
        }
        drawBox(c);
        drawBt(c);
    }

    private void drawBox(Canvas c) {
        paint.setARGB(255, 0, 0, 0);
        paint.setStyle(Paint.Style.FILL);
        int size = index >= 6 ? index - 6 : index;
        if (size > 6) {
            size = 6;
        }
        for (int i = 0; i < size; i++) {
            c.drawCircle(boxStrart + boxWidth * i + boxWidth / 2, boxTop + boxWidth / 2, cicleRe, paint);
        }
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.size);
        if (err > 0) {
            c.drawText("密码输入错误", mScreenWidth / 2, mScreenHeight / 3 - boxWidth, paint);
        } else if (index < 6) {
            c.drawText("输入密码", mScreenWidth / 2, mScreenHeight / 3 - boxWidth, paint);
        } else if (index < 13 && !isOne) {
            c.drawText("确认密码", mScreenWidth / 2, mScreenHeight / 3 - boxWidth, paint);
        }
    }

    private void drawBt(Canvas c) {
        paint.setARGB(255, 186, 186, 186);
        paint.setTextSize(size);
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < 9; i++) {
            paint.setARGB(255, 186, 186, 186);
            paint.setStyle(Paint.Style.STROKE);
            c.drawRect(padding + btWidth * (i % 3), btTop + btWidth * (i / 3), padding + btWidth * (i % 3 + 1), btTop + btWidth * (i / 3 + 1), paint);
            paint.setStyle(Paint.Style.FILL);
            c.drawText(String.valueOf((i + 1)), padding + btWidth * (i % 3) + btWidth / 2, btTop + btWidth * (i / 3) + btWidth / 2 + size / 2, paint);
            if(key==i+1){
                paint.setARGB(65, 116, 116, 116);
                c.drawCircle(padding + btWidth * (i % 3)+btWidth/2,btTop + btWidth * (i / 3)+btWidth/2,keyDwon*(btWidth-10)/16,paint);
            }
        }
        for (int i = 0; i < 3; i++) {
            paint.setStyle(Paint.Style.STROKE);
            c.drawRect(mScreenWidth - padding - btWidth - 20, btTop + btWidth * i, mScreenWidth - padding, btTop + btWidth * (i + 1), paint);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(255, 186, 186, 186);
        c.drawText("<-", mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2 + size / 2, paint);
        if(key==10){
            paint.setARGB(65, 116, 116, 116);
            c.drawCircle(mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2 , keyDwon * (btWidth - 10) / 16, paint);
        }
        paint.setARGB(255, 186, 186, 186);
        c.drawText("0", mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2 * 3 + size / 2, paint);
        if(key==0){
            paint.setARGB(65, 116, 116, 116);
            c.drawCircle(mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2*3,keyDwon*(btWidth-10)/16 , paint);
        }
        paint.setARGB(255, 186, 186, 186);
        c.drawText(index == 0 ? "取消" : "清除", mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2 * 5 + size / 2, paint);
        if(key==11){
            paint.setARGB(65, 116, 116, 116);
            c.drawCircle(mScreenWidth - padding - btWidth / 2 - 10, btTop + btWidth / 2*5 ,keyDwon*(btWidth-10)/16 , paint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isLive = false;
    }

    public void init() {
        mDetector = new GestureDetectorCompat(getContext(), this);
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        mScreenHeight = dm.heightPixels;
        boxLeftMarg = 60;
        boxWidth = 40;
        cicleRe = 16;
        stackWidth = 2;
        boxStrart = boxLeftMarg;
        boxWidth = (mScreenWidth - 2 * boxLeftMarg) / 6;
        boxTop = 60;
        padding = 20;
        btWidth = (mScreenWidth - padding * 2 - 20) / 4;
        btTop = mScreenHeight - btWidth * 4 - padding;
        size = 64;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        int key = getKey(e.getX(), e.getY());
        if (key != -1) {
            add(key);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (getKey(e.getX(), e.getY()) != -1) {
            return true;
        }
        return false;
    }

    private int getKey(float x, float y) {
        if (x > padding && x < padding + btWidth) {
            if (y > btTop && y < btTop + btWidth) {
                return 1;
            } else if (y > btTop + btWidth && y < btTop + btWidth * 2) {
                return 4;
            } else if (y > btTop + btWidth * 2 && y < btTop + btWidth * 3) {
                return 7;
            }
        } else if (x > padding + btWidth && x < padding + btWidth * 2) {
            if (y > btTop && y < btTop + btWidth) {
                return 2;
            } else if (y > btTop + btWidth && y < btTop + btWidth * 2) {
                return 5;
            } else if (y > btTop + btWidth * 2 && y < btTop + btWidth * 3) {
                return 8;
            }
        }
        if (x > padding + btWidth * 2 && x < padding + btWidth * 3) {
            if (y > btTop && y < btTop + btWidth) {
                return 3;
            } else if (y > btTop + btWidth && y < btTop + btWidth * 2) {
                return 6;
            } else if (y > btTop + btWidth * 2 && y < btTop + btWidth * 3) {
                return 9;
            }
        }
        if (x > mScreenWidth - padding - btWidth - 20 && x < mScreenWidth - padding) {
            if (y > btTop && y < btTop + btWidth) {
                return 10;
            } else if (y > btTop + btWidth && y < btTop + btWidth * 2) {
                return 0;
            } else if (y > btTop + btWidth * 2 && y < btTop + btWidth * 3) {
                return 11;
            }
        }
        return -1;
    }

    private void add(int a) {
        key = a;
        keyDwon = 1;
        if (a < 10) {
            if (index < 12) {
                index++;
            }
            if (index < 7) {
                if (TextUtils.isEmpty(t1)) {
                    t1 = "";
                }
                t1 += a;
                if(isOne&&index==6){
                    listener.onTrue(t1);
                }
            } else if (index < 12) {
                if (TextUtils.isEmpty(t2)) {
                    t2 = "";
                }
                t2 += a;
            } else if (index == 12) {
                t2 += a;
                if (TextUtils.equals(t1, t2)) {
                    listener.onTrue(t1);
                } else {
                    if (err == 0) {
                        listener.onErr();
                        err = 1;
                    }
                }
            }
        } else if (key == 10) {
            if (index < 7 && index > 0) {
                if (TextUtils.isEmpty(t1)) {
                    t1 = "";
                }
                if(index==1){
                    t1="";
                }else{
                    t1 = t1.substring(0, index-1);
                }
                index--;
            } else if (index < 12 && index > 6) {
                if (TextUtils.isEmpty(t2)) {
                    t2 = "";
                }
                if(index==7){
                    t2="";
                }else{
                    t2 = t2.substring(0, index - 7);
                }
                index--;
            } else if (index == 12) {
                t2 = t2.substring(0, index - 7);
                err = 0;
                index--;
            }
        } else if (key == 11) {
            if (index == 0) {
                isLive = false;
                listener.onCancle();
            } else if (index > 0) {
                index = 0;
                t1 = "";
                t2 = "";
                err = 0;
            }
        }
        holder();
    }
    public void setIsOne(boolean flag){
        isOne=flag;
    }

    public int getmScreenWidth() {
        return mScreenWidth;
    }

    public int getmScreenHeight() {
        return mScreenHeight;
    }

    public void setListener(InputListener listener) {
        this.listener = listener;
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }
    @Override
    public void onLongPress(MotionEvent e) {
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
