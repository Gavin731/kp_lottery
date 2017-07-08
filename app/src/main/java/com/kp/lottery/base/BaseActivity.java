package com.kp.lottery.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kp.lottery.R;
import com.kp.lottery.kplib.app.KPActivity;


public abstract class BaseActivity extends KPActivity {

    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setTitle();
        initData();
        initView();
    }

    public abstract  void setTitle();

    public abstract void initData();

    public abstract void initView();


    /**
     * 标题右边按钮
     *
     * @param titleName
     * @return
     */
    public View initTitleRightView(boolean bool, String titleName) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_title_right_submit, null);
        TextView textView = (TextView) view.findViewById(R.id.submit);
        textView.setText(titleName);
        return view;
    }

    public View initTitleRightView(String titleName) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_title_right_submit, null);
        TextView textView = (TextView) view.findViewById(R.id.submit);
        textView.setText(titleName);
        return view;
    }

    public void showCenterToast(String text) {
        Toast toast = Toast.makeText(this, "\n" + text + "\n", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showProgressDialog(boolean isScreenLock) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.progress_wait), true, false);
            progressDialog.setCanceledOnTouchOutside(false);// dialog外点击取消dialog
            progressDialog.setCancelable(!isScreenLock);// 返回键有效
        }
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public AlertDialog showAlertDialog(String message, String positiveBtnName, DialogInterface.OnClickListener positiveOnClick) {
        return showAlertDialog(message, positiveBtnName, positiveOnClick, null, null);
    }

    public AlertDialog showAlertDialog(String message, String positiveBtnName, DialogInterface.OnClickListener positiveOnClick, String negativeBtnName, DialogInterface.OnClickListener negativeOnClick) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(positiveBtnName, positiveOnClick);
        if (!TextUtils.isEmpty(negativeBtnName)) {
            alertDialog.setNegativeButton(negativeBtnName, negativeOnClick);
        }
        return alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }










}
