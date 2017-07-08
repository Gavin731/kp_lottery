package com.kp.lottery.kplib.app;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kp.lottery.kplib.log.KPLog;
import com.kp.lottery.kplib.utils.SystemUtils;


public class KPApplication extends Application {

	private static KPApplication instance;

    private Activity currentActivity;

	/**
	 * Application life cycle manager
	 */
	private static int liveCounter;
	private static int activeCounter;	

	public static KPApplication getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Application has not been created");
		}

		return instance;
	}

	/**
	 * Your SHOULDN'T NEVER call this method yourself!
	 */
	public KPApplication() {
		instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (!SystemUtils.isDebuggable(this)) {
			KPLog.isPrint = false;
		}
	}


	
	public void onApplicationStart() {
		KPLog.i("KPApplication::onApplicationStart");
	}
	
	public void onApplicationResume() {
		KPLog.i("KPApplication::onApplicationResume");
	}
	
	public void onApplicationPause() {
		KPLog.i("KPApplication::onApplicationPause");

	}
	
	public void onApplicationStop() {
		KPLog.i("KPApplication::onApplicationStop");
		
	}

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    private static Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if ((--activeCounter) == 0) {
					KPApplication.getInstance().onApplicationPause();
				}
			}
		}
	};

	
	public void activityOnCreate(Activity a) {
		if (liveCounter++ == 0) {
			onApplicationStart();
		}
	}

	public void activityOnResume(Activity a) {
		if (activeCounter++ == 0) {
			onApplicationResume();
		}
	}

	public void activityOnPause(Activity a) {
		handler.sendEmptyMessageDelayed(1, 100);
	}
	
	public void activityOnDestroy(Activity a) {
		if ((--liveCounter) == 0) {
			onApplicationStop();
		}
	}
}
