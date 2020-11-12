package com.feng.freader.view.activity.biz;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.feng.freader.R;
import com.feng.freader.constant.EventBusCode;
import com.feng.freader.db.DatabaseManager;
import com.feng.freader.entity.data.BookshelfNovelDbData;
import com.feng.freader.entity.eventbus.Event;
import com.feng.freader.util.EventBusUtil;
import com.feng.freader.util.ScreenUtil;
import com.feng.freader.util.SpUtil;
import com.feng.freader.widget.PageView;
import com.feng.freader.widget.RealPageView;

import java.util.List;

public class ReadBiz {
    Context context;
    boolean mIsSystemBrightness;
    private DatabaseManager mDbManager;
    ReadIView iView;
    public ReadBiz (Context context,ReadIView iView){
        this.context = context;
        this.iView = iView;
        // 监听系统亮度的变化
        context. getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                true,
                mBrightnessObserver);
        // 其他
        mDbManager = DatabaseManager.getInstance();
    }




    // 监听系统亮度的变化
    public ContentObserver mBrightnessObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (mIsSystemBrightness) {
                // 屏幕亮度更新为新的系统亮度
                ScreenUtil.setWindowBrightness((Activity) context,
                        (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
            }
        }
    };

    /**
     * 进入夜间模式
     */
    public void nightMode(boolean mIsNightMode, View mTheme0, View mTheme1, View mTheme2, View mTheme3, View mTheme4,
                           ImageView mDayAndNightModeIv, TextView mDayAndNightModeTv,TextView mNovelTitleTv,TextView mNovelProgressTv,
                           TextView mStateTv,final RealPageView mPageView) {
        mIsNightMode = true;
        // 取消主题
        mTheme0.setSelected(false);
        mTheme1.setSelected(false);
        mTheme2.setSelected(false);
        mTheme3.setSelected(false);
        mTheme4.setSelected(false);
        // 设置图标和文字
        mDayAndNightModeIv.setImageResource(R.drawable.read_day);
        mDayAndNightModeTv.setText(context.getResources().getString(R.string.read_day_mode));
        // 设置相关颜色
        mNovelTitleTv.setTextColor(context.getResources().getColor(R.color.read_night_mode_title));
        mNovelProgressTv.setTextColor(context.getResources().getColor(R.color.read_night_mode_title));
        mStateTv.setTextColor(context.getResources().getColor(R.color.read_night_mode_text));
        mPageView.setBgColor(context.getResources().getColor(R.color.read_night_mode_bg));
        mPageView.setTextColor(context.getResources().getColor(R.color.read_night_mode_text));
        mPageView.setBackBgColor(context.getResources().getColor(R.color.read_night_mode_back_bg));
        mPageView.setBackTextColor(context.getResources().getColor(R.color.read_night_mode_back_text));
        mPageView.post(new Runnable() {
            @Override
            public void run() {
                mPageView.updateBitmap();
            }
        });
    }

    /**
     * 进入白天模式
     */
    public void dayMode(boolean mIsNightMode,ImageView mDayAndNightModeIv,TextView mDayAndNightModeTv,RealPageView mPageView) {
        mIsNightMode = false;
        // 设置图标和文字
        mDayAndNightModeIv.setImageResource(R.drawable.read_night);
        mDayAndNightModeTv.setText(context.getResources().getString(R.string.read_night_mode));
        // 根据主题进行相关设置
        mPageView.post(new Runnable() {
            @Override
            public void run() {
                iView.updateWithTheme();
            }
        });
    }
    /**
     * 隐藏设置栏
     */
    public void hideSettingBar(final ConstraintLayout mSettingBarCv) {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                context, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSettingBarCv.setVisibility(View.GONE);
                iView.setMIsShowSettingBar(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSettingBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 根据主题更新阅读界面
     */
    public void updateWithTheme(boolean mIsNightMode, View mTheme0, View mTheme1, View mTheme2, View mTheme3, View mTheme4,
                                ImageView mDayAndNightModeIv, TextView mDayAndNightModeTv,TextView mNovelTitleTv,TextView mNovelProgressTv,
                                TextView mStateTv,final RealPageView mPageView,int mTheme) {
        if (mIsNightMode) {
            // 退出夜间模式
            mDayAndNightModeIv.setImageResource(R.drawable.read_night);
            mDayAndNightModeTv.setText(context.getResources().getString(R.string.read_night_mode));
            mIsNightMode = false;
        }
        mTheme0.setSelected(false);
        mTheme1.setSelected(false);
        mTheme2.setSelected(false);
        mTheme3.setSelected(false);
        mTheme4.setSelected(false);
        int bgColor = context.getResources().getColor(R.color.read_theme_0_bg);
        int textColor = context.getResources().getColor(R.color.read_theme_0_text);
        int backBgColor = context.getResources().getColor(R.color.read_theme_0_back_bg);
        int backTextColor = context.getResources().getColor(R.color.read_theme_0_back_text);
        switch (mTheme) {
            case 0:
                mTheme0.setSelected(true);
                bgColor = context.getResources().getColor(R.color.read_theme_0_bg);
                textColor = context.getResources().getColor(R.color.read_theme_0_text);
                backBgColor = context.getResources().getColor(R.color.read_theme_0_back_bg);
                backTextColor = context.getResources().getColor(R.color.read_theme_0_back_text);
                break;
            case 1:
                mTheme1.setSelected(true);
                bgColor = context.getResources().getColor(R.color.read_theme_1_bg);
                textColor = context.getResources().getColor(R.color.read_theme_1_text);
                backBgColor = context.getResources().getColor(R.color.read_theme_1_back_bg);
                backTextColor = context.getResources().getColor(R.color.read_theme_1_back_text);
                break;
            case 2:
                mTheme2.setSelected(true);
                bgColor = context.getResources().getColor(R.color.read_theme_2_bg);
                textColor = context.getResources().getColor(R.color.read_theme_2_text);
                backBgColor = context.getResources().getColor(R.color.read_theme_2_back_bg);
                backTextColor = context.getResources().getColor(R.color.read_theme_2_back_text);
                break;
            case 3:
                mTheme3.setSelected(true);
                bgColor = context.getResources().getColor(R.color.read_theme_3_bg);
                textColor = context.getResources().getColor(R.color.read_theme_3_text);
                backBgColor = context.getResources().getColor(R.color.read_theme_3_back_bg);
                backTextColor = context.getResources().getColor(R.color.read_theme_3_back_text);
                break;
            case 4:
                mTheme4.setSelected(true);
                bgColor = context.getResources().getColor(R.color.read_theme_4_bg);
                textColor = context.getResources().getColor(R.color.read_theme_4_text);
                backBgColor = context.getResources().getColor(R.color.read_theme_4_back_bg);
                backTextColor = context.getResources().getColor(R.color.read_theme_4_back_text);
                break;
        }
        // 设置相关颜色
        mNovelTitleTv.setTextColor(textColor);
        mNovelProgressTv.setTextColor(textColor);
        mStateTv.setTextColor(textColor);
        mPageView.setTextColor(textColor);
        mPageView.setBgColor(bgColor);
        mPageView.setBackTextColor(backTextColor);
        mPageView.setBackBgColor(backBgColor);
        mPageView.updateBitmap();
        if (PageView.IS_TEST) {
            mPageView.setBackgroundColor(bgColor);
            mPageView.invalidate();
        }
    }


    public void deleteBookshelfNovel(String mNovelUrl){
        mDbManager.deleteBookshelfNovel(mNovelUrl);
    }
    public void onDestroy(float mTextSize, float mRowSpace, int mTheme,
                          float mBrightness, boolean mIsNightMode, int mTurnType){

        // 将相关数据存入 SP
        SpUtil.saveTextSize(mTextSize);
        SpUtil.saveRowSpace(mRowSpace);
        SpUtil.saveTheme(mTheme);
        SpUtil.saveBrightness(mBrightness);
        SpUtil.saveIsNightMode(mIsNightMode);
        SpUtil.saveTurnType(mTurnType);

        // 解除监听
        context.getContentResolver().unregisterContentObserver(mBrightnessObserver);
    }


    public boolean ismIsSystemBrightness() {
        return mIsSystemBrightness;
    }

    public void setmIsSystemBrightness(boolean mIsSystemBrightness) {
        this.mIsSystemBrightness = mIsSystemBrightness;
    }
}