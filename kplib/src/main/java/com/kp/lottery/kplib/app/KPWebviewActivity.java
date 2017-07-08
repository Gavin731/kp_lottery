package com.kp.lottery.kplib.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kp.lottery.kplib.R;
import com.kp.lottery.kplib.widget.TitleBar;


public class KPWebviewActivity extends KPActivity {

    protected WebView webView;
    protected ProgressBar progressBar;
    protected String url;
    protected String alertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchParams();
        setupView();
        setupWebView();
        loadUrl(url);
    }

    protected void fetchParams() {
        Uri uri = getIntent().getData();
        url = uri.getQueryParameter("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, getString(R.string.web_url_empty), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            Toast.makeText(this, getString(R.string.web_url_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        alertMessage = uri.getQueryParameter("alertmessage");
    }


    protected void setupView() {
        setContentView(R.layout.layout_webview);
        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        if (getTitleBar().getTitleStyle() == TitleBar.CUSTOM_TITLE) {
            getTitleBar().setLeftView(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    goBack();
                }
            });
            getTitleBar().addRightViewItem("close", getString(R.string.close), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView.canGoBack()) {
                        if (!TextUtils.isEmpty(alertMessage)) {
                        } else {
                            new AlertDialog.Builder(KPWebviewActivity.this)
                                    .setTitle(getString(R.string.whether_close_alert))
                                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            hideKeyboard(webView);
                                            finish();
                                        }
                                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    } else {
                        hideKeyboard(webView);
                        finish();
                    }
                }
            });
        }
    }

    protected void setupWebView() {
        WebSettings settings = webView.getSettings();
        setupWebSettings(settings);

        webView.setWebViewClient(createWebViewClient());
        webView.setWebChromeClient(createChromeWebViewClient());
    }

    protected void loadUrl(String url) {
        webView.loadUrl(url);
    }

    protected void setupWebSettings(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(false);
        settings.setGeolocationEnabled(true);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    protected void goBack() {
        hideKeyboard(webView);
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    protected WebViewClient createWebViewClient() {
        return new QCWebViewClient();
    }

    protected WebChromeClient createChromeWebViewClient() {
        return new QCWebChromeClient();
    }

    public class QCWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel://")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public class QCWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (getTitleBar().getTitleStyle() == TitleBar.CUSTOM_TITLE) {
                getTitleBar().setTitle(title);
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }
    }

}
