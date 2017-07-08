package com.kp.lottery.kplib.image;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.StatFs;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.kp.lottery.kplib.app.KPApplication;
import com.kp.lottery.kplib.log.KPLog;
import com.kp.lottery.kplib.utils.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

public class ImageDiskCache implements DiskCache {

    // Default disk cache size
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    private volatile DiskLruCache diskCache;

    private final Object diskCacheLock = new Object();
    private File disCacheDir;

    private static class ImageDiskCacheHolder {
        public static ImageDiskCache instance = new ImageDiskCache();
    }

    public static ImageDiskCache getInstance() {
        return ImageDiskCacheHolder.instance;
    }

    private ImageDiskCache() {
        disCacheDir = getDiskCacheDir(KPApplication.getInstance(), "image");
    }

    private DiskLruCache getDiskCache() throws IOException {
        if(diskCache == null || diskCache.isClosed()) {
            synchronized (ImageDiskCache.class) {
                if (diskCache == null || diskCache.isClosed()) {
                    File diskCacheDir = disCacheDir;
                    if (diskCacheDir != null) {
                        if (!diskCacheDir.exists()) {
                            diskCacheDir.mkdirs();
                        }
                        if (getUsableSpace(diskCacheDir) > DEFAULT_DISK_CACHE_SIZE) {
                            diskCache = DiskLruCache.open(diskCacheDir, 1, 1,
                                    DEFAULT_DISK_CACHE_SIZE);
                        } else {
                            throw new IOException("have no usable space");
                        }
                    }
                }
            }
        }
        return diskCache;
    }

    @Override
    public File get(Key key) {
        final String cacheKey = hashKeyForDisk(key);
        File result = null;
        try {
            final DiskLruCache.Value value = getDiskCache().get(cacheKey);
            if (value != null) {
                result = value.getFile(0);
            }
            KPLog.d("Disk cache get: Key " + cacheKey);
        } catch (IOException e) {
            KPLog.e("getBitmapFromDiskCache - " + e);
        }
        return result;
    }

    @Override
    public void put(Key key, Writer writer) {
        final String cacheKey = hashKeyForDisk(key);
        synchronized (diskCacheLock) {
            try {
                DiskLruCache.Editor editor = getDiskCache().edit(cacheKey);
                // Editor will be null if there are two concurrent puts. In the worst case we will just silently fail.
                if (editor != null) {
                    try {
                        File file = editor.getFile(0);
                        if (writer.write(file)) {
                            editor.commit();
                        }
                    } finally {
                        editor.abortUnlessCommitted();
                    }
                }
                KPLog.d("Disk cache put: Key " + cacheKey);
            } catch (final IOException e) {
                KPLog.e("addBitmapToCache - " + e);
            }
        }
    }

    @Override
    public void delete(Key key) {
        String cacheKey = hashKeyForDisk(key);
        synchronized (diskCacheLock) {
            try {
                getDiskCache().remove(cacheKey);
                KPLog.d("Disk cache removed: Key " + key);
            } catch (IOException e) {
                KPLog.e("removeCache - " + e);
            }
        }
    }

    public long getDiskCacheSize() {
        if (diskCache != null) {
            return diskCache.size();
        }
        return 0;
    }

    @Override
    public void clear() {
        synchronized (diskCacheLock) {
            try {
                getDiskCache().delete();
                KPLog.d("Disk cache cleared");
            } catch (IOException e) {
                KPLog.e("clearCache - " + e);
            }
            diskCache = null;
        }
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context    The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir
        // otherwise use internal cache dir
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(
                context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    @TargetApi(VERSION_CODES.GINGERBREAD)
    public static boolean isExternalStorageRemovable() {
        if (VersionUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (VersionUtils.hasFroyo()) {

            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @TargetApi(VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path) {
        if (VersionUtils.hasGingerbread()) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable
     * for using as a disk filename.
     */
    private static String hashKeyForDisk(Key key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            key.updateDiskCacheKey(mDigest);
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (Exception e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


}
