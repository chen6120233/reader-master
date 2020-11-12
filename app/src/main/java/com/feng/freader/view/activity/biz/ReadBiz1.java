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

public class ReadBiz1 {
    Context context;
    boolean mIsSystemBrightness;
    private DatabaseManager mDbManager;
    ReadIView iView;
    public ReadBiz1(Context context){
        this.context = context;

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


    public void deleteBookshelfNovel(String mNovelUrl){
        mDbManager.deleteBookshelfNovel(mNovelUrl);
    }
    public void onDestroy(boolean mIsNeedWrite2Db, String mNovelUrl, boolean mIsReverse, int mChapterIndex, List<String> mChapterUrlList,
                          int mType, String mName, String mCover, RealPageView mPageView, float mTextSize, float mRowSpace, int mTheme,
                          float mBrightness, boolean mIsNightMode, int mTurnType){
        if (mIsNeedWrite2Db) {
            // 将书籍信息存入数据库
            mDbManager.deleteBookshelfNovel(mNovelUrl);
            if (mIsReverse) {   // 如果倒置了目录的话，需要倒置章节索引
                mChapterIndex = mChapterUrlList.size() - 1 - mChapterIndex;
            }
            if (mType == 0 || mType == 1) {
                BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                        mCover, mChapterIndex, mPageView.getPosition(), mType);
                mDbManager.insertBookshelfNovel(dbData);
            } else if (mType == 2){
                BookshelfNovelDbData dbData = new BookshelfNovelDbData(mNovelUrl, mName,
                        mCover, mChapterIndex, mPageView.getFirstPos(), mType, mPageView.getSecondPos());
                mDbManager.insertBookshelfNovel(dbData);
            }
        }

        // 更新书架页面数据
        Event event = new Event(EventBusCode.BOOKSHELF_UPDATE_LIST);
        EventBusUtil.sendEvent(event);

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