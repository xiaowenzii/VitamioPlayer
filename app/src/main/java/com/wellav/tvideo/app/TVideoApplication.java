package com.wellav.tvideo.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by JingWen.Li on 2017/11/22.
 */

public class TVideoApplication extends HBaseApp {

    static TVideoApplication s_instance;

    @Override
    public void onCreate() {
        super.onCreate();
        s_instance = this;
    }

    /**
     * @return
     */
    public static TVideoApplication getInstance() {
        return s_instance;
    }

}
