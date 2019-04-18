package com.wellav.tvideo.ui;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;

import io.vov.vitamio.widget.VideoView;

public class FullScreenView extends VideoView {
    private Context context;

    public FullScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public FullScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FullScreenView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = dm.getDisplays();
        int maxWidth = -1, maxHeight = -1;
        DisplayMetrics met = new DisplayMetrics();
        for (Display d : displays) {
            d.getMetrics(met);
            maxWidth = met.widthPixels > maxWidth ? met.widthPixels : maxWidth;
            maxHeight = met.heightPixels > maxHeight ? met.heightPixels : maxHeight;
        }
        setMeasuredDimension(maxWidth, maxHeight);
    }
}