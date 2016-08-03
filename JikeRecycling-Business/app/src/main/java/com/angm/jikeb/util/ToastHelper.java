package com.angm.jikeb.util;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.angm.jikeb.R;

import java.lang.ref.SoftReference;

/**
 * Must be init first, or will throw IllegalStateException
 */
public class ToastHelper {

    private static ToastImpl toastImpl;

    public static void init(Application app) {
        toastImpl = new ToastImpl(app);
    }

    private static void assertInit() {
        if (toastImpl == null) {
            throw new IllegalStateException("ToastHelper need be init first..");
        }
    }

    public static void cancel() {
        assertInit();
        toastImpl.cancel();
    }

    public static void showToast(CharSequence message) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_SHORT);
    }

    public static void showToast(CharSequence message, int gravity, int xofsset,
                                 int yoffset) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_SHORT, gravity, xofsset,
                yoffset);
    }

    public static void showLongToast(CharSequence message) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_LONG);
    }

    public static void showLongToast(CharSequence message, int gravity, int xofsset,
                                     int yoffset) {
        assertInit();
        toastImpl.showToast(message, Toast.LENGTH_LONG, gravity, xofsset,
                yoffset);
    }


    //
    private static class ToastImpl {

        private Application app;
        private LayoutInflater inflater;
        private SoftReference<Toast> softRefToast;
        private static Handler handler = new Handler(Looper.getMainLooper());

        ToastImpl(Application app) {
            this.app = app;
            inflater = LayoutInflater.from(app);
        }

        void cancel() {
            if (softRefToast != null && softRefToast.get() != null) {
                softRefToast.get().cancel();
            }
        }

        void showToast(CharSequence message, int duration) {
            showToast(message, duration, null, null);
        }

        void showToast(CharSequence message, int duration, int gravity,
                       int xofsset, int yoffset) {
            showToast(message, duration, new GravityBean(gravity, xofsset,
                    yoffset), null);
        }

        @SuppressWarnings("unused")
        void showToast(CharSequence message, int duration,
                       float horizontalMargin, float verticalMargin) {
            showToast(message, duration, null, new MarginBean(horizontalMargin,
                    verticalMargin));
        }

        private void showToast(final CharSequence message, final int duration,
                               final GravityBean gravity, final MarginBean margin) {
            handler.post(new Runnable() {
                public void run() {
                    Toast toast = new Toast(app);
                    toast.setDuration(duration);
                    if (gravity != null) {
                        toast.setGravity(gravity.gravity, gravity.xoffset,
                                gravity.yoffset);
                    }
                    if (margin != null) {
                        toast.setMargin(margin.horizontalMargin,
                                margin.verticalMargin);
                    }
                    View content = inflater.inflate(R.layout.common_toast, null);
                    TextView textView = (TextView) content.findViewById(R.id.text_toast);
                    toast.setView(content);
                    textView.setText(message);

                    synchronized (ToastImpl.this) {
                        if (softRefToast != null && softRefToast.get() != null) {
                            softRefToast.get().cancel();
                        }
                        softRefToast = new SoftReference<Toast>(toast);
                        toast.show();
                    }
                }
            });
        }

        private class GravityBean {

            int gravity;
            int xoffset;
            int yoffset;

            public GravityBean(int g, int xo, int yo) {
                gravity = g;
                xoffset = xo;
                yoffset = yo;
            }
        }

        private class MarginBean {

            float horizontalMargin;
            float verticalMargin;

            public MarginBean(float hm, float vm) {
                horizontalMargin = hm;
                verticalMargin = vm;
            }
        }

    }


}
