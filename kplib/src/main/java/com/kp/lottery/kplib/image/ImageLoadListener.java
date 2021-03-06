package com.kp.lottery.kplib.image;

import android.graphics.Bitmap;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public abstract class ImageLoadListener implements RequestListener<String, Bitmap> {

    public abstract void onImageLoadError(Exception e, String path);

    public abstract void onImageLoadFinish(Bitmap bitmap, String path);

    @Override
    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
        onImageLoadError(e, model);
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        onImageLoadFinish(resource, model);
        return false;
    }
}
