package com.kp.lottery.main.activity;

import android.os.Bundle;

import com.kp.lottery.R;
import com.kp.lottery.kplib.app.KPFragmentTabActivity;
import com.kp.lottery.kplib.widget.TitleBar;
import com.kp.lottery.main.fragment.HomeFragment;
import com.kp.lottery.main.fragment.ManagerFragment;
import com.kp.lottery.main.fragment.MeFragment;

public class MainTabActivity extends KPFragmentTabActivity {

    @Override
    protected TitleBar initTitleBar() {
        return new TitleBar(this, TitleBar.NO_TITLE);
    }


    @Override
    protected void setOnContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addTab("home", R.layout.item_tab_home, HomeFragment.class, null);
        addTab("manager", R.layout.item_tab_manager, ManagerFragment.class, null);
        addTab("me", R.layout.item_tab_me, MeFragment.class, null);
    }

}
