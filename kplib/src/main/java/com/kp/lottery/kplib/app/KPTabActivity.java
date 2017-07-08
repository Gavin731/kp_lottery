package com.kp.lottery.kplib.app;

import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.kp.lottery.kplib.widget.TitleBar;


public class KPTabActivity extends TabActivity {
	
	private TitleBar titleBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		titleBar = initTitleBar();
		
		KPApplication.getInstance().activityOnCreate(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		KPApplication.getInstance().activityOnResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		KPApplication.getInstance().activityOnPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		KPApplication.getInstance().activityOnDestroy(this);
	}
	
	protected TitleBar initTitleBar() {
		return new TitleBar(this, TitleBar.NO_TITLE);
	}
	
	public TitleBar getTitleBar() {
		return titleBar;
	}
	
	public void startActivity(String urlSchema) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)));
	}
	
	public void startActivityForResult(String urlSchema, int requestCode) {
		startActivityForResult(
				new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)),
				requestCode);
	}

}
