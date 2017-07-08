package com.kp.lottery.base;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kp.lottery.R;


public abstract class BaseAdapter extends android.widget.BaseAdapter {

    public static final Object LOADING = new Object();

    protected View getListViewLoadingView(ViewGroup parent, View convertView) {
        View v = convertView == null ? null : convertView.getTag() == LOADING ? convertView : null;
        if (v == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            v = inflater.inflate(R.layout.item_listview_loading, parent, false);
            v.setTag(LOADING);
        }
        return v;
    }

    public void showCenterToast(Context context, String text) {
        Toast toast = Toast.makeText(context, "\n" + text + "\n", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
