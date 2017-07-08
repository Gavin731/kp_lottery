package com.kp.lottery.base;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.kp.lottery.kplib.app.KPFragment;


public class BaseFragment extends KPFragment {


    public void showCenterToast(Context context, String text) {
        Toast toast = Toast.makeText(context, "\n" + text + "\n", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
