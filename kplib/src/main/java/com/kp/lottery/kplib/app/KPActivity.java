package com.kp.lottery.kplib.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kp.lottery.kplib.R;
import com.kp.lottery.kplib.log.KPLog;
import com.kp.lottery.kplib.utils.ActivityAnimationStyle;
import com.kp.lottery.kplib.widget.TitleBar;


public class KPActivity extends FragmentActivity {

    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar = initTitleBar();
        if (titleBar.getTitleStyle() == TitleBar.CUSTOM_TITLE) {
            titleBar.setTitle(getTitle());
            titleBar.setLeftView(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(v);
                    KPActivity.this.finish();
                }
            });
        }
        KPApplication.getInstance().activityOnCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KPApplication.getInstance().activityOnResume(this);
        KPApplication.getInstance().setCurrentActivity(this);
    }


    public void finish(int slideStyle) {
        super.finish();
        setAnimationStyle(slideStyle);
    }

    @Override
    public void finish() {
        finish(ActivityAnimationStyle.STYLE_SLIDE_OUT);
    }

    private void setAnimationStyle(int slideStyle) {
        switch (slideStyle) {
            case ActivityAnimationStyle.STYLE_SLIDE_OUT:
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_OUT:
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                break;
            case ActivityAnimationStyle.STYLE_SLIDE_IN:
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_IN:
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_IN:
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_OUT:
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        KPApplication.getInstance().activityOnPause(this);
        if (this.equals(KPApplication.getInstance().getCurrentActivity())) {
            KPApplication.getInstance().setCurrentActivity(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KPApplication.getInstance().activityOnDestroy(this);
        if (this.equals(KPApplication.getInstance().getCurrentActivity())) {
            KPApplication.getInstance().setCurrentActivity(null);
        }
    }

    protected TitleBar initTitleBar() {
        return new TitleBar(this, TitleBar.CUSTOM_TITLE);
    }

    private static final String TAG = KPActivity.class.getSimpleName();

    public TitleBar getTitleBar() {
        return titleBar;
    }


    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivity(String urlSchema) {
        startActivity(urlSchema, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivity(String urlSchema, int pushStyle) {
        if (TextUtils.isEmpty(urlSchema)) {
            KPLog.e("startActivity java.lang.Null: URL to null ");
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), pushStyle);
    }


    public void startActivity(Intent intent, int pushStyle) {
        startActivityForResult(intent, -1, pushStyle);
    }


    public void startActivityForResult(String urlSchema, int requestCode) {
        startActivityForResult(urlSchema, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivityForResult(String urlSchema, int requestCode, int pushStyle) {
        if (TextUtils.isEmpty(urlSchema)) {
            return;
        }
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), requestCode, pushStyle);
    }


    public void startActivityForResult(Intent intent, int requestCode, int pushStyle) {
        super.startActivityForResult(intent, requestCode);
        setAnimationStyle(pushStyle);
    }

    /**
     * 关闭软键盘
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 开启软键盘
     */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
