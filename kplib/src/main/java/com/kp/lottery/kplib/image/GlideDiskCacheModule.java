package com.kp.lottery.kplib.image;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

public class GlideDiskCacheModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(ImageDiskCache.getInstance());
    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
