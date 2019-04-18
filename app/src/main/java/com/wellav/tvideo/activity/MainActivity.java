package com.wellav.tvideo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.tvideo.R;
import com.wellav.tvideo.adapter.ChannelListAdapter;
import com.wellav.tvideo.entity.Channel;
import com.wellav.tvideo.entity.Channels;
import com.wellav.tvideo.sysconfig.SysConfig;
import com.wellav.tvideo.utils.Utils;

import java.sql.Timestamp;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnSeekCompleteListener, AdapterView.OnItemClickListener {

    private final static float CHANNEL_DIS = 150f;
    private final static float VOLUME_DIS = 50f;
    private float mAspectRatio = 0;
    private ImageView imageView;
    private TextView nametTextView;
    private TextView nameTV;
    private TextView timeTextView;
    private ListView channelList;
    private VideoView videoView;
    private FrameLayout fLayout;
    private boolean isPlay;
    private boolean isLock;
    private String timeString;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private View mVolumeBrightnessLayout;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mVolume = -1;
    private float mBrightness = -1f;
    private float mMoveDis = -1f;
    // 改变节目优先
    private Boolean isChangeChannel = false;
    private GestureDetector mGestureDetector;
    private long pauseSize;
    private int size;
    private static int mCurrentIndex = 0;
    private ChannelListAdapter mChannelAdapter;
    private Channels mChannelData;
    private Channel mCurrentChannel;
    //是否触屏
    private boolean isTouchScreen;
    //缓存亮度值
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        if (!Vitamio.isInitialized(this)) {
            return;
        }
        setContentView(R.layout.activity_main);
        getInitDate();
        initView();
        setInitDate();
        play();

    }

    private void getInitDate() {
        mCurrentIndex = 0;
        mChannelData = (Channels) getIntent().getSerializableExtra("channels");
        size = mChannelData.getContentList().size();
        if (mCurrentIndex > size - 1) {
            mCurrentIndex = 0;
        }
        mCurrentChannel = mChannelData.getContentList().get(mCurrentIndex);

        mChannelData.setCode(mCurrentIndex);
        mSharedPreferences = this.getSharedPreferences("session", MODE_PRIVATE);
    }

    private void setInitDate() {
        nametTextView.setText(mCurrentChannel.getName());
        nameTV.setText(mCurrentChannel.getName());
        isTouchScreen = false;

        //设置初始亮度
        setBright(false, 0);
    }

    private void initView() {
        fLayout = findViewById(R.id.title_fl);
        fLayout.setVisibility(View.GONE);
        videoView = findViewById(R.id.surface_view);
        imageView = findViewById(R.id.image_lock);
        imageView.setOnClickListener(this);
        imageView.setVisibility(View.GONE);
        nameTV = findViewById(R.id.name);
        channelList = findViewById(R.id.channel_list);
        nametTextView = findViewById(R.id.movie_name);
        nametTextView.setOnClickListener(this);
        timeTextView = findViewById(R.id.movie_time);

        timeString = (new Timestamp(System.currentTimeMillis())).toString().substring(11, 16);
        timeTextView.setText(timeString);

        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = findViewById(R.id.operation_bg);
        mOperationPercent = findViewById(R.id.operation_percent);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mChannelAdapter = new ChannelListAdapter(this, mChannelData);
        channelList.setAdapter(mChannelAdapter);
        channelList.setOnItemClickListener(this);
        channelList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isTouchScreen = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 触摸移动时的操作
                        break;
                    case MotionEvent.ACTION_UP:
                        // 触摸抬起时的操作
                        disHandler.removeMessages(2);
                        disHandler.sendEmptyMessageDelayed(2, 5000);
                        break;
                }
                return false;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = getWindowManager().getDefaultDisplay();

            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            mMoveDis = mOldX - x;
            float moveY = Math.abs(mOldY - y);
            if (Math.abs(mOldX - x) > moveY) {
                isChangeChannel = true;
            } else {
                isChangeChannel = false;
            }
            if (!isChangeChannel) {
                if (mOldX > windowWidth * 2.0 / 3 && moveY > VOLUME_DIS) {
                    onVolumeSlide((mOldY - y) / windowHeight);
                } else if (mOldX < windowWidth * 1.0 / 3.0 && moveY > VOLUME_DIS) {
                    onBrightnessSlide((mOldY - y) / (windowHeight * 4));
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.e("onSingleTapConfirmed", "onSingleTapConfirmed");
            endGesture();

            if (isLock) {
                if (imageView.getVisibility() == View.VISIBLE) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }
                return true;
            }

            // 点击时隐藏声音和亮度视图
            if (mVolumeBrightnessLayout.getVisibility() == View.VISIBLE) {
                mVolumeBrightnessLayout.setVisibility(View.GONE);
            }

            if (channelList.getVisibility() == View.VISIBLE) {
                channelList.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            } else {
                if (imageView.getVisibility() == View.VISIBLE) {
                    imageView.setVisibility(View.GONE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }

                if (channelList.getVisibility() == View.GONE && imageView.getVisibility() == View.VISIBLE) {
                    mChannelAdapter.refresh(mChannelData);
                    channelList.setVisibility(View.VISIBLE);
                } else {
                    channelList.setVisibility(View.GONE);
                }
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    public void onBack(View view) {
        if (videoView != null) {
            videoView.stopPlayback();
            finish();
        }
    }

    /**
     * 切换节目
     *
     * @param dis
     */
    private boolean isChanging = false;

    private void changeChannel(float dis) {
        if (isLock) {
            return;
        }
        if (dis > CHANNEL_DIS) {
            if (!isChanging) {
                isChanging = true;
                nextChannel();
            }
        } else if (dis < -CHANNEL_DIS) {
            if (!isChanging) {
                isChanging = true;
                prevChannel();
            }
        }
        mChannelAdapter.refresh(mChannelData);
    }

    public void onShowChannel(View view) {
        fLayout.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        mChannelAdapter.refresh(mChannelData);
        channelList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (videoView != null) {
            videoView.stopPlayback();
            finish();
        }
        super.onBackPressed();
    }

    @SuppressLint("WrongViewCast")
    private void onVolumeSlide(float percent) {
        if (isLock) {
            return;
        }
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0) {
                mVolume = 0;
            }

            mOperationBg.setImageResource(R.mipmap.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    private void onBrightnessSlide(float percent) {
        if (isLock) {
            return;
        }
        mOperationBg.setImageResource(R.mipmap.video_brightness_bg);
        mVolumeBrightnessLayout.setVisibility(View.VISIBLE);

        setBright(true, percent);
    }

    private void play() {
        isPlay = true;
        try {
            String url = mCurrentChannel.getPlayUrl();
            videoView.setVideoURI(Uri.parse(Environment.getExternalStorageDirectory() + "/12/" + url));
            videoView.setOnCompletionListener(this);
            videoView.setOnBufferingUpdateListener(this);
            videoView.setOnErrorListener(this);
            videoView.setOnInfoListener(this);
            videoView.setOnPreparedListener(this);
        } catch (Exception e) {
            Utils.PrintLog("Exception", "PlayActivity " + e.toString());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        Utils.PrintLog("hck", "surfaceCreated");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        //设置视频缓冲大小, 默认1024KB, 单位byte
        videoView.setBufferSize(1024);
        if (pauseSize > 0) {
            videoView.seekTo(pauseSize);
        }
        pauseSize = 0;
        isChanging = false;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        videoView.stopPlayback();
        nextChannel();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, mAspectRatio);
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        Utils.PrintLog("hck", "error :" + arg1 + arg2);
        // 处理播放过程中播放出错问题
        play();
        // return false;
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        switch (arg1) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (isPlay) {
                    videoView.pause();
                    isPlay = false;
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isPlay) {
                    videoView.start();
                    isPlay = true;
                }
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // 点击时隐藏声音和亮度视图
                if (mVolumeBrightnessLayout.getVisibility() == View.VISIBLE) {
                    mVolumeBrightnessLayout.setVisibility(View.GONE);
                }

                disHandler.removeMessages(0);
                disHandler.removeMessages(1);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                if (isChangeChannel) {
                    changeChannel(mMoveDis);
                }
                endGesture();
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        mMoveDis = -1f;

        disHandler.removeMessages(0);
        disHandler.sendEmptyMessageDelayed(0, 1000);
        disHandler.removeMessages(1);
        disHandler.sendEmptyMessageDelayed(1, 3000);
    }

    @SuppressLint("HandlerLeak")
    private Handler disHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mVolumeBrightnessLayout.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                fLayout.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                if (!isTouchScreen) {
                    channelList.setVisibility(View.GONE);
                    isTouchScreen = false;
                }
            } else {
                channelList.setVisibility(View.GONE);
                isTouchScreen = false;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_lock:
                if (isLock) {
                    isLock = false;
                    imageView.setBackgroundResource(R.mipmap.lock_off);
                } else {
                    isLock = true;
                    imageView.setBackgroundResource(R.mipmap.lock_on);
                    fLayout.setVisibility(View.GONE);
                    channelList.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void prevChannel() {
        if (videoView != null && mCurrentIndex != size - 1) {
            videoView.stopPlayback();
            mCurrentIndex++;

        } else if (videoView != null && mCurrentIndex == size - 1) {
            videoView.stopPlayback();
            mCurrentIndex = 0;
        }
        channelList.setSelection(mCurrentIndex);
        channelList.smoothScrollToPosition(mCurrentIndex);
        mChannelData.setCode(mCurrentIndex);
        mCurrentChannel = mChannelData.getContentList().get(mCurrentIndex);
        setInitDate();
        play();
    }

    private void nextChannel() {
        if (videoView != null && mCurrentIndex != 0) {
            videoView.stopPlayback();
            mCurrentIndex--;
        } else if (videoView != null && mCurrentIndex == 0) {
            videoView.stopPlayback();
            mCurrentIndex = size - 1;
        }
        channelList.setSelection(mCurrentIndex);
        channelList.smoothScrollToPosition(mCurrentIndex);
        mChannelData.setCode(mCurrentIndex);
        mCurrentChannel = mChannelData.getContentList().get(mCurrentIndex);
        setInitDate();
        play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlay) {
            setBright(false, 0);
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
        isPlay = false;
        System.gc();
    }

    @Override
    public void onSeekComplete(MediaPlayer arg0) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (videoView != null) {
            videoView.stopPlayback();
            mCurrentIndex = position;
            mChannelData.setCode(mCurrentIndex);
            mCurrentChannel = mChannelData.getContentList().get(mCurrentIndex);
            setInitDate();
            play();
            mChannelAdapter.refresh(mChannelData);

            //设置重新隐藏的时间
            isTouchScreen = true;
            disHandler.removeMessages(2);
            disHandler.sendEmptyMessageDelayed(2, 5000);
        }
    }

    /**
     * 设置亮度
     */
    @SuppressLint("WrongViewCast")
    private void setBright(boolean isCommit, float percent) {
        mBrightness = mSharedPreferences.getFloat(SysConfig.VIDEO_BRIGHT, -1f);
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        if (isCommit) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putFloat(SysConfig.VIDEO_BRIGHT, lpa.screenBrightness);
            editor.commit();
        }
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }
}
