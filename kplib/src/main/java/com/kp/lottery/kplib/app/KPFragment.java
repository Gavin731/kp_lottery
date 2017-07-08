package com.kp.lottery.kplib.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.kp.lottery.kplib.R;
import com.kp.lottery.kplib.log.KPLog;
import com.kp.lottery.kplib.utils.ActivityAnimationStyle;

public class KPFragment extends Fragment {

    private static final String TAG = KPFragment.class.getSimpleName();

    public void startActivity(String urlSchema) {
        this.startActivity(urlSchema, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }


    public void startActivity(String urlSchema, int pushStyle) {
        if (TextUtils.isEmpty(urlSchema)) {
            KPLog.e("startActivity java.lang.Null: URL to null ");
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), pushStyle);
    }

    public void startActivity(Intent intent, int pushStyle) {
        super.startActivity(intent);
        setPushAnimationStyle(pushStyle);
    }

    @Override
    public void startActivity(Intent intent) {
        if (isAdded()) {
            startActivity(intent, ActivityAnimationStyle.STYLE_SLIDE_IN);
        } else {
            KPLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ");
        }
    }

    /**
     * Activity Animation Style
     */
    private void setPushAnimationStyle(int pushStyle) {
        switch (pushStyle) {
            case ActivityAnimationStyle.STYLE_SLIDE_OUT:
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_OUT:
                getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
                break;
            case ActivityAnimationStyle.STYLE_SLIDE_IN:
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case ActivityAnimationStyle.STYLE_MODAL_IN:
                getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_IN:
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                break;
            case ActivityAnimationStyle.STYLE_RIGHT_SLIDE_OUT:
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, ActivityAnimationStyle.STYLE_SLIDE_IN);
    }

    public void startActivityForResult(Intent intent, int requestCode, int pushStyle) {
        if (isAdded()) {
            if (intent == null) {
                return;
            }
            super.startActivityForResult(intent, requestCode);
            setPushAnimationStyle(pushStyle);
        } else {
            KPLog.e("startActivity java.lang.IllegalStateException: Fragment not attached to Activity ");
        }
    }

}
