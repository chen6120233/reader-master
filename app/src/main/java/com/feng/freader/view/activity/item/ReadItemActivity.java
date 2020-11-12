package com.feng.freader.view.activity.item;

import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.feng.freader.R;
import com.feng.freader.base.BaseActivity;
import com.feng.freader.constant.EventBusCode;
import com.feng.freader.entity.Next;
import com.feng.freader.entity.data.BookshelfNovelDbData;
import com.feng.freader.entity.data.DetailedChapterData;
import com.feng.freader.entity.eventbus.Event;
import com.feng.freader.presenter.ReadPresenter;
import com.feng.freader.util.EventBusUtil;
import com.feng.freader.util.LogUtil;
import com.feng.freader.util.ScreenUtil;
import com.feng.freader.util.SpUtil;
import com.feng.freader.util.StatusBarUtil;
import com.feng.freader.view.activity.biz.ReadBiz;
import com.feng.freader.view.activity.biz.ReadIView;
import com.feng.freader.widget.PageView;
import com.feng.freader.widget.RealPageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import data.SharedPreferencesUtils;

/**
 * 小说阅读界面
 *
 * @author Feng Zhaohao
 *         Created on 2019/11/25
 */

public class ReadItemActivity extends BaseActivity
        implements View.OnClickListener,ReadIView {
    private static final String LOADING_TEXT = "正在加载中…";
    public static final String KEY_POSITION = "read_key_position";
    private RealPageView mPageView;
    private TextView mNovelTitleTv;
    private TextView mNovelProgressTv;
    private TextView mStateTv;
    private RelativeLayout mTopSettingBarRv;
    private ConstraintLayout mBottomBarCv;
    private ConstraintLayout mBrightnessBarCv;
    private ConstraintLayout mSettingBarCv;
    private ImageView mBackIv;
    private ImageView mMenuIv;
    private TextView mPreviousChapterTv;
    private SeekBar mNovelProcessSb;
    private TextView mCatalogProgressTv;
    private TextView mNextChapterTv;
    private ImageView mCatalogIv;
    private ImageView mBrightnessIv;
    private ImageView mDayAndNightModeIv;
    private ImageView mSettingIv;
    private TextView mCatalogTv;
    private TextView mBrightnessTv;
    private TextView mDayAndNightModeTv;
    private TextView mSettingTv;

    private SeekBar mBrightnessProcessSb;
    private Switch mSystemBrightnessSw;

    private ImageView mDecreaseFontIv;
    private ImageView mIncreaseFontIv;
    private ImageView mDecreaseRowSpaceIv;
    private ImageView mIncreaseRowSpaceIv;
    private View mTheme0;
    private View mTheme1;
    private View mTheme2;
    private View mTheme3;
    private View mTheme4;
    private TextView mTurnNormalTv;
    private TextView mTurnRealTv;
    private int mPosition;  // 文本开始读取位置
    private int mPosition1;


    private boolean mIsLoadingChapter = false;  // 是否正在加载具体章节
    private boolean mIsShowingOrHidingBar = false;  // 是否正在显示或隐藏上下栏
    private boolean mIsShowBrightnessBar = false;   // 是否正在显示亮度栏
    private boolean mIsSystemBrightness = true;     // 是否为系统亮度
    private boolean mIsShowSettingBar = false;      // 是否正在显示设置栏

    // 从 sp 中读取
    private float mTextSize;    // 字体大小
    private float mRowSpace;    // 行距
    private int mTheme;         // 阅读主题
    private float mBrightness;  // 屏幕亮度，为 -1 时表示系统亮度
    private boolean mIsNightMode;           // 是否为夜间模式
    private int mTurnType;      // 翻页模式：0 为正常，1 为仿真

    private float mMinTextSize = 36f;
    private float mMaxTextSize = 76f;
    private float mMinRowSpace = 0f;
    private float mMaxRowSpace = 48f;

    private boolean isNext = true;

    HashMap<String, DetailedChapterData> list = new HashMap<>();

    @Override
    protected void doBeforeSetContentView() {
        StatusBarUtil.setLightColorStatusBar(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_item;
    }

    @Override
    protected ReadPresenter getPresenter() {
        return new ReadPresenter();
    }

    ReadBiz biz;
    DetailedChapterData data;

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        biz = new ReadBiz(this,this);
        biz.setmIsSystemBrightness(mIsSystemBrightness);
        mPosition = getIntent().getIntExtra(KEY_POSITION, 0);
        // 从 SP 得到
        mTextSize = SpUtil.getTextSize();
        mRowSpace = SpUtil.getRowSpace();
        mTheme = SpUtil.getTheme();
        mBrightness = SpUtil.getBrightness();
        mIsNightMode = SpUtil.getIsNightMode();
        mTurnType = SpUtil.getTurnType();


    }

    @Override
    protected void initView() {
        mTopSettingBarRv = findViewById(R.id.rv_read_top_bar);
        mBottomBarCv = findViewById(R.id.cv_read_bottom_bar);
        mBrightnessBarCv = findViewById(R.id.cv_read_brightness_bar);
        mSettingBarCv = findViewById(R.id.cv_read_setting_bar);

        mPageView = findViewById(R.id.pv_read_page_view);
        mPageView.setPageViewListener(new PageView.PageViewListener() {
            @Override
            public void updateProgress(String progress) {

                mNovelProgressTv.setText(progress);
            }

            @Override
            public void next() {
                nextNet();
            }

            @Override
            public void pre() {
                preNet();

            }

            @Override
            public void nextPage() {
                updateChapterProgress();

            }

            @Override
            public void prePage() {
                updateChapterProgress();

            }

            @Override
            public void showOrHideSettingBar() {
                if (mIsShowingOrHidingBar) {
                    return;
                }
                if (mIsShowBrightnessBar) {
                    hideBrightnessBar();
                    return;
                }
                if (mIsShowSettingBar) {
                    biz. hideSettingBar(mSettingBarCv);
                    return;
                }
                mIsShowingOrHidingBar = true;
                if (mTopSettingBarRv.getVisibility() != View.VISIBLE) {
                    // 显示上下栏
                    showBar();
                } else {
                    // 隐藏上下栏
                    hideBar();
                }
            }
        });

        mNovelTitleTv = findViewById(R.id.tv_read_novel_title);
        mNovelProgressTv = findViewById(R.id.tv_read_novel_progress);
        mStateTv = findViewById(R.id.tv_read_state);

        mBackIv = findViewById(R.id.iv_read_back);
        mBackIv.setOnClickListener(this);
        mMenuIv = findViewById(R.id.iv_read_menu);
        mMenuIv.setOnClickListener(this);
        mPreviousChapterTv = findViewById(R.id.tv_read_previous_chapter);
        mPreviousChapterTv.setOnClickListener(this);
        mNextChapterTv = findViewById(R.id.tv_read_next_chapter);
        mNextChapterTv.setOnClickListener(this);
        mCatalogIv = findViewById(R.id.iv_read_catalog);
        mCatalogIv.setOnClickListener(this);
        mBrightnessIv = findViewById(R.id.iv_read_brightness);
        mBrightnessIv.setOnClickListener(this);
        mDayAndNightModeIv = findViewById(R.id.iv_read_day_and_night_mode);
        mDayAndNightModeIv.setOnClickListener(this);
        mSettingIv = findViewById(R.id.iv_read_setting);
        mSettingIv.setOnClickListener(this);
        mCatalogTv = findViewById(R.id.tv_read_catalog);
        mCatalogTv.setOnClickListener(this);
        mBrightnessTv = findViewById(R.id.tv_read_brightness);
        mBrightnessTv.setOnClickListener(this);
        mDayAndNightModeTv = findViewById(R.id.tv_read_day_and_night_mode);
        mDayAndNightModeTv.setOnClickListener(this);
        mSettingTv = findViewById(R.id.tv_read_setting);
        mSettingTv.setOnClickListener(this);

        mNovelProcessSb = findViewById(R.id.sb_read_novel_progress);

        mCatalogProgressTv = findViewById(R.id.tv_read_catalog_progress);
        mNovelProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double scale = (double) progress / 100f;
                mCatalogProgressTv.setText(data.getName());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mCatalogProgressTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mCatalogProgressTv.setVisibility(View.GONE);
                showChapter();
            }
        });

        mBrightnessProcessSb = findViewById(R.id.sb_read_brightness_bar_brightness_progress);
        mBrightnessProcessSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mIsSystemBrightness) {
                    // 调整亮度
                    mBrightness = (float) progress / 100;
                    ScreenUtil.setWindowBrightness(ReadItemActivity.this, mBrightness);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSystemBrightnessSw = findViewById(R.id.sw_read_system_brightness_switch);
        mSystemBrightnessSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 变为系统亮度
                    mIsSystemBrightness = true;
                    mBrightness = -1f;
                    // 将屏幕亮度设置为系统亮度
                    ScreenUtil.setWindowBrightness(ReadItemActivity.this,
                            (float) ScreenUtil.getSystemBrightness() / ScreenUtil.getBrightnessMax());
                } else {
                    // 变为自定义亮度
                    mIsSystemBrightness = false;
                    // 将屏幕亮度设置为自定义亮度
                    mBrightness = (float) mBrightnessProcessSb.getProgress() / 100;
                    ScreenUtil.setWindowBrightness(ReadItemActivity.this, mBrightness);
                }
            }
        });

        mDecreaseFontIv = findViewById(R.id.iv_read_decrease_font);
        mDecreaseFontIv.setOnClickListener(this);
        mIncreaseFontIv = findViewById(R.id.iv_read_increase_font);
        mIncreaseFontIv.setOnClickListener(this);
        mDecreaseRowSpaceIv = findViewById(R.id.iv_read_decrease_row_space);
        mDecreaseRowSpaceIv.setOnClickListener(this);
        mIncreaseRowSpaceIv = findViewById(R.id.iv_read_increase_row_space);
        mIncreaseRowSpaceIv.setOnClickListener(this);
        mTheme0 = findViewById(R.id.v_read_theme_0);
        mTheme0.setOnClickListener(this);
        mTheme1 = findViewById(R.id.v_read_theme_1);
        mTheme1.setOnClickListener(this);
        mTheme2 = findViewById(R.id.v_read_theme_2);
        mTheme2.setOnClickListener(this);
        mTheme3 = findViewById(R.id.v_read_theme_3);
        mTheme3.setOnClickListener(this);
        mTheme4 = findViewById(R.id.v_read_theme_4);
        mTheme4.setOnClickListener(this);
        mTurnNormalTv = findViewById(R.id.tv_read_turn_normal);
        mTurnNormalTv.setOnClickListener(this);
        mTurnRealTv = findViewById(R.id.tv_read_turn_real);
        mTurnRealTv.setOnClickListener(this);
        switch (mTurnType) {
            case 0:
                mTurnNormalTv.setSelected(true);
                mPageView.setTurnType(PageView.TURN_TYPE.NORMAL);
                break;
            case 1:
                mTurnRealTv.setSelected(true);
                mPageView.setTurnType(PageView.TURN_TYPE.REAL);
                break;
        }
    }

    @Override
    protected void doAfterInit() {
        if (mBrightness == -1f) {    // 系统亮度
            mSystemBrightnessSw.setChecked(true);
        } else {    // 自定义亮度
            mBrightnessProcessSb.setProgress((int) (100 * mBrightness));
            mSystemBrightnessSw.setChecked(false);
            ScreenUtil.setWindowBrightness(this, mBrightness);
        }

        if (mIsNightMode) { // 夜间模式
            biz.nightMode( mIsNightMode,  mTheme0,  mTheme1,  mTheme2,  mTheme3,  mTheme4,
                     mDayAndNightModeIv,  mDayAndNightModeTv, mNovelTitleTv, mNovelProgressTv,
                     mStateTv, mPageView);
        } else {    // 日间模式
            biz.dayMode( mIsNightMode, mDayAndNightModeIv, mDayAndNightModeTv, mPageView);
        }


    }

    @Override
    public void setMIsShowSettingBar(boolean mIsShowSettingBar) {
        this.mIsShowSettingBar = mIsShowSettingBar;
    }

    @Override
    protected void onPause() {
        super.onPause();
        biz.onDestroy( mTextSize,  mRowSpace,  mTheme,
         mBrightness,  mIsNightMode,  mTurnType);
    }

    @Override
    protected void onStop() {
        List<BookshelfNovelDbData> mDataList = new ArrayList<>();
        if(SharedPreferencesUtils.contains(this,"list")){
            mDataList.addAll(( List<BookshelfNovelDbData>)SharedPreferencesUtils.getBean(this,"list"));
        }
        int b=0;
        if(data!=null){
            for (int i = 0; i < mDataList.size(); i++) {
                String s = data.getUrl().split("/")[data.getUrl().split("/").length-2];
                LogUtil.v(s+"==="+ mDataList.get(i).getCover());
                if(mDataList.get(i).getCover()!=null){
                    String[] split = mDataList.get(i).getCover().split("/");
                    if(split.length>1){
                        String s1 = split[split.length-2];
                        LogUtil.v(s+"==="+ mDataList.get(i).getCover());
                        if(!TextUtils.isEmpty(s)){
                            if(s.equals(s1)){
                                b=1;
                                mDataList.get(i).setNovelUrl(CustomActivity.URL.getUrl());
                                mDataList.get(i).setCover(!data.getPrev_url().equals("")?data.getPrev_url():data.getNext_url());
                                mDataList.get(i).setPosition(mPageView.getPosition());
                                mDataList.get(i).setName(data.getName());
                            }
                        }
                    }

                }


            }
            if(b==0){
                BookshelfNovelDbData bookshelfNovelDbData = new BookshelfNovelDbData();
                bookshelfNovelDbData.setNovelUrl(CustomActivity.URL.getUrl());
                LogUtil.v(!data.getPrev_url().equals("")?data.getPrev_url():data.getNext_url());
                bookshelfNovelDbData.setCover(!data.getPrev_url().equals("")?data.getPrev_url():data.getNext_url());
                bookshelfNovelDbData.setPosition(mPageView.getPosition());
                bookshelfNovelDbData.setType(4);
                bookshelfNovelDbData.setName(data.getName());
                mDataList.add(bookshelfNovelDbData);
            }

            SharedPreferencesUtils.putBean(this,"list",mDataList);
        }
        Event event = new Event(EventBusCode.BOOKSHELF_UPDATE_LIST);
        EventBusUtil.sendEvent(event);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Override
    protected boolean isRegisterEventBus() {
        return false;
    }
    /**获取下一页*/
    void getDetailedChapterData(DetailedChapterData data) {


            if (isNext) {
                if (!TextUtils.isEmpty(data.getNext_url())) {
                    Next next = new Next();
                    next.setUrl(data.getNext_url());
                    EventBus.getDefault().post(next);
                } else {
                    Toast.makeText(this, "到最后一页", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (!TextUtils.isEmpty(data.getPrev_url())) {
                    Next next = new Next();
                    next.setUrl(data.getPrev_url());
                    EventBus.getDefault().post(next);
                } else {
                    Toast.makeText(this, "到最前一页", Toast.LENGTH_SHORT).show();
                }
            }
        }




    /**
     * 获取具体章节信息成功
     */
    public void getDetailedChapterDataSuccess(final DetailedChapterData data) {
        mIsLoadingChapter = false;
        if (data == null) {
            mStateTv.setText("获取不到相关数据，请查看其他章节");
            return;
        }
        mStateTv.setVisibility(View.GONE);
        if (isNext) {
            mPageView.initDrawText(data.getContent(), mPosition);
        } else {
            LogUtil.v(mPosition1+"=================");
            mPageView.initDrawText(data.getContent(), mPosition1);
            mPosition1 = 0;
        }
        mNovelTitleTv.setText(data.getName());
        updateChapterProgress();

    }


    /**
     * 点击上一页/下一页后加载具体章节
     */
    private void showChapter() {
        mIsLoadingChapter = true;
        mPageView.clear();              // 清除当前文字
        mStateTv.setVisibility(View.VISIBLE);
        mStateTv.setText(LOADING_TEXT);
        mPosition = 0;     // 归零
        Iterator iter = list.keySet().iterator();

        int b = 0;
        while (iter.hasNext()) {
            /**已有缓存*/
            String key = (String) iter.next();
            DetailedChapterData data1 = (DetailedChapterData) list.get(key);
            String next[] = data.getNext_url().split("/");
            String ss1[] = data1.getUrl().split("/");
            String prev[] = data.getPrev_url().split("/");
            if (isNext) {
                /**下一页*/
                if(mPageView.getPosition()!=0){
                    mPosition1 = mPageView.getPosition();
                }
                if (ss1[ss1.length - 1].equals(next[next.length - 1])) {
                    data = data1;
                    b = 1;
                    getDetailedChapterDataSuccess(data1);
                    getDetailedChapterData(data);
                }
            } else {
                /**上一页*/
                if (ss1[ss1.length - 1].equals(prev[prev.length - 1])) {
                    data = data1;
                    b = 1;
                    LogUtil.v(data.getName());
                    getDetailedChapterDataSuccess(data1);
                    LogUtil.v(data.getName());
                    getDetailedChapterData(data);
                }
            }
        }
        if (b == 0) {
            /**没有缓存，获取数据*/
            getDetailedChapterData(data);
        }


        updateChapterProgress();
    }

    /**
     * 显示上下栏
     */
    private void showBar() {
        Animation topAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_top_enter);
        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                StatusBarUtil.setDarkColorStatusBar(ReadItemActivity.this);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 结束时重置标记
                mIsShowingOrHidingBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mTopSettingBarRv.startAnimation(topAnim);
        mBottomBarCv.startAnimation(bottomAnim);
        mTopSettingBarRv.setVisibility(View.VISIBLE);
        mBottomBarCv.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏上下栏
     */
    private void hideBar() {
        Animation topExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_top_exit);
        topExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTopSettingBarRv.setVisibility(View.GONE);
                mIsShowingOrHidingBar = false;
                StatusBarUtil.setLightColorStatusBar(ReadItemActivity.this);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBarCv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTopSettingBarRv.startAnimation(topExitAnim);
        mBottomBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 显示亮度栏
     */
    private void showBrightnessBar() {
        mIsShowBrightnessBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mBrightnessBarCv.startAnimation(bottomAnim);
        mBrightnessBarCv.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏亮度栏
     */
    private void hideBrightnessBar() {
        Animation bottomExitAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_exit);
        bottomExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBrightnessBarCv.setVisibility(View.GONE);
                mIsShowBrightnessBar = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBrightnessBarCv.startAnimation(bottomExitAnim);
    }

    /**
     * 显示设置栏
     */
    private void showSettingBar() {
        mIsShowSettingBar = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(
                this, R.anim.read_setting_bottom_enter);
        mSettingBarCv.startAnimation(bottomAnim);
        mSettingBarCv.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateWithTheme() {
        biz.updateWithTheme( mIsNightMode,  mTheme0,  mTheme1,  mTheme2,  mTheme3,  mTheme4,
                mDayAndNightModeIv,  mDayAndNightModeTv, mNovelTitleTv, mNovelProgressTv,
                mStateTv, mPageView, mTheme);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_read_back:
                finish();
                break;
            case R.id.iv_read_menu:
                break;
            case R.id.tv_read_previous_chapter:
                // 加载上一章节
                preNet();

                break;
            case R.id.tv_read_next_chapter:
                // 加载下一章节
                nextNet();
                break;
            case R.id.iv_read_catalog:
            case R.id.tv_read_catalog:
                // 目录
                finish();
                break;
            case R.id.iv_read_brightness:
            case R.id.tv_read_brightness:
                // 隐藏上下栏，并显示亮度栏
                hideBar();
                showBrightnessBar();
                break;
            case R.id.iv_read_day_and_night_mode:
            case R.id.tv_read_day_and_night_mode:
                if (!mIsNightMode) {    // 进入夜间模式
                    biz.nightMode( mIsNightMode,  mTheme0,  mTheme1,  mTheme2,  mTheme3,  mTheme4,
                            mDayAndNightModeIv,  mDayAndNightModeTv, mNovelTitleTv, mNovelProgressTv,
                            mStateTv, mPageView);
                } else {    // 进入日间模式
                    biz.dayMode( mIsNightMode, mDayAndNightModeIv, mDayAndNightModeTv, mPageView);
                }
                hideBar();
                break;
            case R.id.iv_read_setting:
            case R.id.tv_read_setting:
                // 隐藏上下栏，并显示设置栏
                hideBar();
                showSettingBar();
                break;
            case R.id.iv_read_decrease_font:
                if (mTextSize == mMinTextSize) {
                    break;
                }
                mTextSize--;
                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_increase_font:
                if (mTextSize == mMaxTextSize) {
                    break;
                }
                mTextSize++;
                mPageView.setTextSize(mTextSize);
                break;
            case R.id.iv_read_decrease_row_space:
                if (mRowSpace == mMinRowSpace) {
                    break;
                }
                mRowSpace--;
                mPageView.setRowSpace(mRowSpace);
                break;
            case R.id.iv_read_increase_row_space:
                if (mRowSpace == mMaxRowSpace) {
                    break;
                }
                mRowSpace++;
                mPageView.setRowSpace(mRowSpace);
                break;
            case R.id.v_read_theme_0:
                if (!mIsNightMode && mTheme == 0) {
                    break;
                }
                mTheme = 0;
                updateWithTheme();
                break;
            case R.id.v_read_theme_1:
                if (!mIsNightMode && mTheme == 1) {
                    break;
                }
                mTheme = 1;
                updateWithTheme();
                break;
            case R.id.v_read_theme_2:
                if (!mIsNightMode && mTheme == 2) {
                    break;
                }
                mTheme = 2;
                updateWithTheme();
                break;
            case R.id.v_read_theme_3:
                if (!mIsNightMode && mTheme == 3) {
                    break;
                }
                mTheme = 3;
                updateWithTheme();
                break;
            case R.id.v_read_theme_4:
                if (!mIsNightMode && mTheme == 4) {
                    break;
                }
                mTheme = 4;
                updateWithTheme();
                break;
            case R.id.tv_read_turn_normal:
                if (mTurnType != 0) {
                    mTurnType = 0;
                    mTurnNormalTv.setSelected(true);
                    mTurnRealTv.setSelected(false);
                    mPageView.setTurnType(PageView.TURN_TYPE.NORMAL);
                }
                break;
            case R.id.tv_read_turn_real:
                if (mTurnType != 1) {
                    mTurnType = 1;
                    mTurnRealTv.setSelected(true);
                    mTurnNormalTv.setSelected(false);
                    mPageView.setTurnType(PageView.TURN_TYPE.REAL);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 网络小说加载上一章节
     */
    private void preNet() {
        isNext = false;
        showChapter();
    }


    /**
     * 网络小说加载下一章节
     */
    private void nextNet() {
        isNext = true;
        showChapter();
    }


    /**
     * 更新章节进度条的进度
     */
    private void updateChapterProgress() {
        int progress = 0;
        mNovelProcessSb.setProgress(progress);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DetailedChapterData data) {
        if (this.data == null) {
            this.data = data;
            getDetailedChapterDataSuccess(data);
            getDetailedChapterData(data);
        }
        list.put(data.getName(), data);
        if (mIsLoadingChapter) {
            getDetailedChapterDataSuccess(data);
        }

    }
}
