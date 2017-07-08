package com.kp.lottery.kplib.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public final class ImageLoader {

    public static void loadImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).asBitmap().into(imageView);
    }

    public static void loadImage(Context context, String path, ImageView imageView, ImageLoadOptions options) {
        loadImage(context, path, imageView, options, null);
    }

    public static void loadImage(Context context, String path, ImageView imageView, ImageLoadOptions options, ImageLoadListener listener) {
        BitmapRequestBuilder requestBuilder = Glide.with(context).load(path).asBitmap();

        if(options.shouldShowImageOnLoading()) {
            requestBuilder.placeholder(options.getImageOnLoading());
        }

        if(options.shouldShowImageForEmpty()){
            requestBuilder.fallback(options.getImageForEmpty());
        }

        if(options.shouldShowImageOnFail()) {
            requestBuilder.error(options.getImageOnFail());
        }

        if(!options.isCacheInMemory()) {
            requestBuilder.skipMemoryCache(true);
        }

        if(!options.isCacheOnDisk()) {
            requestBuilder.diskCacheStrategy(DiskCacheStrategy.NONE);
        }

        if(options.getWidth() > -1  && options.getHeight() > -1) {
            requestBuilder.override(options.getWidth(), options.getHeight());
        }

        if(listener != null) {
            requestBuilder.listener(listener);
        }
        requestBuilder.into(imageView);
    }

    public static long sizeOfDiskCache(Context context) {
        return ImageDiskCache.getInstance().getDiskCacheSize();
    }

    public static void clearCache(final Context context) {
        Glide.get(context).clearMemory();
        ImageDiskCache.getInstance().clear();
    }

}
