package com.feng.freader.view.activity.item;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import android.webkit.JavascriptInterface;
import android.widget.TextView;


import com.feng.freader.R;
import com.feng.freader.entity.Next;
import com.feng.freader.entity.data.DetailedChapterData;
import com.feng.freader.util.LogUtil;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Web2Activity extends AppCompatActivity {

    WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);
        mWeb = findViewById(R.id.web);
        EventBus.getDefault().register(this);
        setWeb();
        findViewById(R.id.fanhui).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView mokuai_text =  findViewById(R.id.mokuai_text);
        mokuai_text.setText(CustomActivity.URL.getName());
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    private void setWeb() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWeb.getSettings().setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = mWeb.getSettings().getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(mWeb.getSettings(), true);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        mWeb.removeJavascriptInterface("searchBoxJavaBredge");//禁止远程代码执行
        final WebSettings settings = mWeb.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
//        settings.setSupportZoom(true);          //支持缩放
        settings.setBuiltInZoomControls(false);  //不启用内置缩放装置
        settings.setJavaScriptEnabled(true);    //启用JS脚
        settings.setUserAgentString(null);
        // settings.setLoadsImagesAutomatically(false);//不加载图片
        mWeb.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        // 打印结果
        try {
            mWeb.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                            + "document.getElementsByTagName('html')[0].innerHTML+'</head>','" + url + "');");
                    LogUtil.v(url);
                    if (url.contains(CustomActivity.URL.getUrl())||url.contains("baidu")) {

                    }

                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, String s) {

                    return super.shouldOverrideUrlLoading(webView, s);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                    String s= url.split("\\.")[1];
                    String s1= CustomActivity.URL.getUrl().split("\\.")[1];
                    LogUtil.v(s+"=="+s1+"=contains="+url);
                    if(s.equals(s1)||url.contains(CustomActivity.URL.getUrl())||url.contains(".js")||url.contains("baidu")){
                        return super.shouldInterceptRequest(view, url);
                    }
                    LogUtil.v("="+url);
                    return new WebResourceResponse(null,null,null);
                }
            });
        } catch (Exception e) {
        }
        /**
         * 访问服务器返回的数据处理
         * action：返回数据类型
         * error：返回是否成功
         * data：返回的数据,格式Json
         * */
        mWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                LogUtil.v("onReceivedTitleon==" + s);


            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
            }

           /* @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                //解析参数message
                //调用java方法并得到结果


                //返回结果
                result.confirm("result");

                return true;
            }*/
        });
        url = CustomActivity.URL.getUrl()+ (!TextUtils.isEmpty(CustomActivity.URL.getNext())?CustomActivity.URL.getNext():"");
        mWeb.loadUrl(url);

    }

    String url = "https://m.heiyan.la/shu/5420/1058253.html";

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html, String url) {

            if (url.contains("html")) {
                String titleName = "";
                String href = "";
                String prev = "";
                StringBuffer sb = new StringBuffer();
                Document document = Jsoup.parse(html);


                final DetailedChapterData data = setData(document, titleName, href, prev, sb, url);
                LogUtil.v(data.toString());
                if(TextUtils.isEmpty(data.getPrev_url())&&TextUtils.isEmpty(data.getNext_url())){
                    LogUtil.v("111111111");
                }else {

                    if (isTopActivity(getTopTask(), getPackageName(), WebActivity.class.getName())) {
                        Intent intent = new Intent(Web2Activity.this, ReadItemActivity.class);
                        startActivity(intent);

                    }

                    //必要延迟发送
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(data);
                        }
                    },500);
                }


            }
        }
    }

    private DetailedChapterData setData(Document document, String titleName, String href, String prev, StringBuffer sb, String url) {
        if(url.contains(CustomActivity.URL.getUrl())){
            if(url.contains(CustomActivity.URL.getCondition())){

                Elements title1 = document.select(CustomActivity.URL.getTitleName());
                if (title1 != null && title1.size() > 0) {
                    titleName = title1.get(0).text();
                }
                Elements e1 = document.select(CustomActivity.URL.getContent());
                for (int i = 0; i < e1.size(); i++) {
                    String text = e1.get(i).text();
                    sb.append(text + "\n");
                }
                if(CustomActivity.URL.getPrev_url().equals(CustomActivity.URL.getNext_url())){
                    Elements pager = document.select(CustomActivity.URL.getNext_url());
                    if (pager != null && pager.size() > 0) {
                        Elements element = pager.get(0).select("a");
                        if (element != null && element.size() > 0) {
                            prev = element.get(0).attr("href");
                            href = element.get(2).attr("href");
                        }
                    }
                }else {
                    Elements prev_url = document.select(CustomActivity.URL.getPrev_url());
                    Elements next_url = document.select(CustomActivity.URL.getNext_url());

                    if (next_url != null && next_url.size() > 0) {
                        href = next_url.get(0).attr("href");
                    }

                    if (prev_url != null && prev_url.size() > 0) {
                        prev = prev_url.get(0).attr("href");
                    }
                }

            }
        }

        DetailedChapterData data = new DetailedChapterData();
        data.setContent(sb.toString());
        data.setName(titleName);
        data.setNext_url(href.contains(".html") ? href : "");
        data.setUrl(url);
        data.setPrev_url(prev.contains(".html") ? prev : "");

        return data;
    }


    protected ActivityManager mActivityManager;

    public ActivityManager.RunningTaskInfo getTopTask() {
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }

        return null;
    }

    public boolean isTopActivity(
            ActivityManager.RunningTaskInfo topTask,
            String packageName,
            String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;

            if (topActivity.getPackageName().equals(packageName) &&
                    topActivity.getClassName().equals(activityName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        if (mWeb.canGoBack()) {
            mWeb.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.destroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Next next) {
        LogUtil.v(next.getUrl());
        if(next.getUrl().contains("http")){
            mWeb.loadUrl(CustomActivity.URL.getUrl());
        }else {
            mWeb.loadUrl(CustomActivity.URL.getUrl()+ next.getUrl());
        }
        // LogUtil.v(CustomActivity.URL.getUrl() + next.getUrl());

    }
}
