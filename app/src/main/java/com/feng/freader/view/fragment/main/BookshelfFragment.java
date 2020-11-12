package com.feng.freader.view.fragment.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feng.freader.R;
import com.feng.freader.adapter.BookshelfNovelsAdapter;
import com.feng.freader.base.BaseFragment;
import com.feng.freader.constant.EventBusCode;
import com.feng.freader.constract.IBookshelfContract;
import com.feng.freader.db.DatabaseManager;
import com.feng.freader.entity.ReadEntity;
import com.feng.freader.entity.data.BookshelfNovelDbData;
import com.feng.freader.entity.epub.OpfData;
import com.feng.freader.entity.eventbus.Event;
import com.feng.freader.presenter.BookshelfPresenter;
import com.feng.freader.util.FileUtil;
import com.feng.freader.util.LogUtil;
import com.feng.freader.util.NetUtil;
import com.feng.freader.util.StatusBarUtil;
import com.feng.freader.view.activity.ReadActivity;
import com.feng.freader.view.activity.item.CustomActivity;
import com.feng.freader.view.activity.item.WebActivity;
import com.feng.freader.widget.TipDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import data.SharedPreferencesUtils;

import static com.feng.freader.view.activity.item.CustomActivity.custom;

/**
 * @author Feng Zhaohao
 *
 * 书架
 * Created on 2019/10/20
 */
public class BookshelfFragment extends BaseFragment<BookshelfPresenter>
        implements View.OnClickListener, IBookshelfContract.View {

    private static final String TAG = "BookshelfFragment";

    private RecyclerView mBookshelfNovelsRv;
    private TextView mLocalAddTv;
    private ImageView mLocalAddIv;
    private RelativeLayout mLoadingRv;

    private RelativeLayout mMultiDeleteRv;
    private TextView mSelectAllTv;
    private TextView mCancelTv;
    private TextView mDeleteTv;

    private List<BookshelfNovelDbData> mDataList = new ArrayList<>();

    private BookshelfNovelsAdapter mBookshelfNovelsAdapter;
    private boolean mIsDeleting = false;

    private DatabaseManager mDbManager;

    @Override
    protected void doInOnCreate() {
        StatusBarUtil.setLightColorStatusBar(getActivity());
        mPresenter.queryAllBook();
    }

    @Override
    protected BookshelfPresenter getPresenter() {
        return new BookshelfPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    protected void initData() {
        mDbManager = DatabaseManager.getInstance();
    }

    @Override
    protected void initView() {
        mBookshelfNovelsRv = getActivity().findViewById(R.id.rv_bookshelf_bookshelf_novels_list);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBookshelfNovelsRv.setLayoutManager(gridLayoutManager);

        mLocalAddTv = getActivity().findViewById(R.id.tv_bookshelf_add);
        mLocalAddTv.setOnClickListener(this);
        mLocalAddIv = getActivity().findViewById(R.id.iv_bookshelf_add);
        mLocalAddIv.setOnClickListener(this);

        mLoadingRv = getActivity().findViewById(R.id.rv_bookshelf_loading);

        mMultiDeleteRv = getActivity().findViewById(R.id.rv_bookshelf_multi_delete_bar);
        mSelectAllTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_select_all);
        mSelectAllTv.setOnClickListener(this);
        mCancelTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_cancel);
        mCancelTv.setOnClickListener(this);
        mDeleteTv = getActivity().findViewById(R.id.tv_bookshelf_multi_delete_delete);
        mDeleteTv.setOnClickListener(this);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCome(Event event) {
        switch (event.getCode()) {
            case EventBusCode.BOOKSHELF_UPDATE_LIST:
                mPresenter.queryAllBook();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_bookshelf_add:
            case R.id.iv_bookshelf_add:
                // 导入本机小说
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");      // 最近文件（任意类型）
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_bookshelf_multi_delete_select_all:
                // 全选
                for (int i = 0; i < mDataList.size(); i++) {
                    mDataList.get(i).setCheck(true);
                }
                mBookshelfNovelsAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_bookshelf_multi_delete_cancel:
                // 取消多选删除
                cancelMultiDelete();
                break;
            case R.id.tv_bookshelf_multi_delete_delete:
                // 删除操作
                if (!deleteCheck()) {
                    break;
                }
                new TipDialog.Builder(getActivity())
                        .setContent("确定要删除这些小说吗？")
                        .setCancel("不了")
                        .setEnsure("确定")
                        .setOnClickListener(new TipDialog.OnClickListener() {
                            @Override
                            public void clickEnsure() {
                                multiDelete();
                            }

                            @Override
                            public void clickCancel() {

                            }
                        })
                        .build()
                        .show();
                break;
            default:
                break;
        }
    }

    /**
     * 取消多选删除
     */
    private void cancelMultiDelete() {
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).setCheck(false);
        }
        mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        mMultiDeleteRv.setVisibility(View.GONE);
        mIsDeleting = false;
    }

    /**
     * 多选删除
     */
    private void multiDelete() {
        mIsDeleting = true;
        for (int i = mDataList.size()-1; i >= 0; i--) {
            if (mDataList.get(i).isCheck()) {
                // 从数据库中删除该小说
                if(mDataList.get(i).getType()!=4){
                    mDbManager.deleteBookshelfNovel(mDataList.get(i).getNovelUrl());
                }else {
                    List<BookshelfNovelDbData> list = (List<BookshelfNovelDbData>) SharedPreferencesUtils.getBean(getActivity(),"list");
                    LogUtil.v(list.size());
                    List<BookshelfNovelDbData> list2 = new ArrayList<>();
                    for (int j = 0; j < list.size(); j++) {
                        LogUtil.v(list.get(i).getName()+"==="+mDataList.get(i).getName());
                        String s = list.get(j).getCover().split("/")[0];
                        String ss = mDataList.get(i).getCover().split("/")[0];
                        if(!s.equals(ss)){
                            list2.add(list.get(i));
                        }
                    }
                    LogUtil.v(list2.size());
                    SharedPreferencesUtils.putBean(getActivity(),"list",list2);
                    List<BookshelfNovelDbData> list1 = (List<BookshelfNovelDbData>) SharedPreferencesUtils.getBean(getActivity(), "list");
                    LogUtil.v(list1.size());
                }

                mDataList.remove(i);
            }
        }


        mBookshelfNovelsAdapter.setIsMultiDelete(false);
        mBookshelfNovelsAdapter.notifyDataSetChanged();
        mMultiDeleteRv.setVisibility(View.GONE);
        mIsDeleting = false;
    }

    private boolean deleteCheck() {
        for (int i = 0; i <mDataList.size() ; i++) {
            if(mDataList.get(i).isCheck()){
                return true;
            }
        }
        showShortToast("请先选定要删除的小说");
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) { // 选择了才继续
            Uri uri = data.getData();
            String filePath = FileUtil.uri2FilePath(getActivity(), uri);
            File file = new File(filePath);
            String fileName = file.getName();
            Log.d(TAG, "onActivityResult: fileLen = " + file.length());
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  // 后缀名
            if (suffix.equals("txt")) {
                if (mDbManager.isExistInBookshelfNovel(filePath)) {
                    showShortToast("该小说已导入");
                    return;
                }
                if (FileUtil.getFileSize(file) > 100) {
                    showShortToast("文件过大");
                    return;
                }
                // 将该小说的数据存入数据库
                BookshelfNovelDbData dbData = new BookshelfNovelDbData(filePath, file.getName(),
                        "", 0, 0, 1);
                mDbManager.insertBookshelfNovel(dbData);
                // 更新列表
                mPresenter.queryAllBook();
            }
            else if (suffix.equals("epub")) {
                if (mDbManager.isExistInBookshelfNovel(filePath)) {
                    showShortToast("该小说已导入");
                    return;
                }
                if (FileUtil.getFileSize(file) > 100) {
                    showShortToast("文件过大");
                    return;
                }
                // 在子线程中解压该 epub 文件
                mLoadingRv.setVisibility(View.VISIBLE);
                mPresenter.unZipEpub(filePath);
            }
            else {
                showShortToast("不支持该类型");
            }
        }
    }

    /**
     * 查询所有书籍信息成功
     */
    @Override
    public void queryAllBookSuccess(List<BookshelfNovelDbData> dataList) {
        if (mBookshelfNovelsAdapter == null) {
            mDataList = dataList;
            if(SharedPreferencesUtils.contains(getActivity(),"list")){
                mDataList.addAll((List<BookshelfNovelDbData>) SharedPreferencesUtils.getBean(getActivity(),"list"));
            }

            initAdapter();
            mBookshelfNovelsRv.setAdapter(mBookshelfNovelsAdapter);
        } else {
            mDataList.clear();
            mDataList.addAll(dataList);
            if(SharedPreferencesUtils.contains(getActivity(),"list")){
                mDataList.addAll((List<BookshelfNovelDbData>) SharedPreferencesUtils.getBean(getActivity(),"list"));
            }

            mBookshelfNovelsAdapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        mBookshelfNovelsAdapter = new BookshelfNovelsAdapter(getActivity(), mDataList,
                new BookshelfNovelsAdapter.BookshelfNovelListener() {
                    @Override
                    public void clickItem(int position) {
                        if (!NetUtil.hasInternet(getActivity())) {
                            showShortToast("当前无网络，请检查网络后重试");
                            return;
                        }
                        if (mIsDeleting) {
                            return;
                        }
                        if(mDataList.get(position).getType()==4){
                            Intent intent = new Intent(getActivity(), WebActivity.class);
                            ReadEntity entity = new ReadEntity();
                            entity.setUrl(mDataList.get(position).getNovelUrl());
                            entity.setNext(mDataList.get(position).getCover());
                            entity.setName(mDataList.get(position).getName());
                            List<ReadEntity> list = (List<ReadEntity>) SharedPreferencesUtils.getBean(getContext(),custom);
                            for (int i = 0; i <list.size() ; i++) {
                                if (list.get(i).getUrl().equals(mDataList.get(position).getNovelUrl())){
                                    list.get(i).setNext(mDataList.get(position).getCover());
                                    CustomActivity.URL =list.get(i);
                                }
                            }

                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getActivity(), ReadActivity.class);
                            // 小说 url
                            intent.putExtra(ReadActivity.KEY_NOVEL_URL, mDataList.get(position).getNovelUrl());
                            // 小说名
                            intent.putExtra(ReadActivity.KEY_NAME, mDataList.get(position).getName());
                            // 小说封面 url
                            intent.putExtra(ReadActivity.KEY_COVER, mDataList.get(position).getCover());
                            // 小说类型
                            intent.putExtra(ReadActivity.KEY_TYPE, mDataList.get(position).getType());
                            // 开始阅读的位置
                            intent.putExtra(ReadActivity.KEY_CHAPTER_INDEX, mDataList.get(position).getChapterIndex());
                            intent.putExtra(ReadActivity.KEY_POSITION, mDataList.get(position).getPosition());
                            intent.putExtra(ReadActivity.KEY_SECOND_POSITION, mDataList.get(position).getSecondPosition());

                            startActivity(intent);
                        }

                    }

                    @Override
                    public void longClick(int position) {

                        mIsDeleting = true;
                        mBookshelfNovelsAdapter.setIsMultiDelete(true);
                        mBookshelfNovelsAdapter.notifyDataSetChanged();
                        mMultiDeleteRv.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 查询所有书籍信息失败
     */
    @Override
    public void queryAllBookError(String errorMsg) {

    }

    /**
     * 解压 epub 文件成功
     */
    @Override
    public void unZipEpubSuccess(String filePath, OpfData opfData) {
        // 将书籍信息写入数据库
        File file = new File(filePath);
        BookshelfNovelDbData dbData = new BookshelfNovelDbData(filePath, file.getName(),
                opfData.getCover(), 0, 0, 2);
        mDbManager.insertBookshelfNovel(dbData);
        // 更新列表
        mPresenter.queryAllBook();

        mLoadingRv.setVisibility(View.GONE);
        Log.d(TAG, "unZipEpubSuccess: opfData = " + opfData);
        showShortToast("导入成功");
    }

    /**
     * 解压 epub 文件失败
     */
    @Override
    public void unZipEpubError(String errorMsg) {
        mLoadingRv.setVisibility(View.GONE);
        Log.d(TAG, "unZipEpubError: " + errorMsg);
        showShortToast("导入失败");
    }
}
