package com.kp.lottery.kplib.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * a textView that is able to self-adjust its font size depending on the min and max size of the font, and its own size.<br/>
 * code is heavily based on this StackOverflow thread:
 * http://stackoverflow.com/questions/16017165/auto-fit-textview-for-android/21851239#21851239 <br/>
 * It should work fine with most Android versions, but might have some issues on Android 3.1 - 4.04, as setTextSize will only work for the first time. <br/>
 * More info here: https://code.google.com/p/android/issues/detail?id=22493 and here in case you wish to fix it: http://stackoverflow.com/a/21851239/878126
 */
public class AutoResizeTextView extends TextView {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF availableSpaceRect = new RectF();
    private final SparseIntArray textCachedSizes = new SparseIntArray();
    private final SizeTester sizeTester;
    private float maxTextSize;
    private float spacingMult = 1.0f;
    private float spacingAdd = 0.0f;
    private float minTextSize;
    private int widthLimit;
    private int maxLines;
    private boolean enableSizeCache = true;
    private boolean initiallized = false;
    private TextPaint paint;

    private interface SizeTester {
        /**
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying {@code suggestedSize} to
         * text, it takes less space than {@code availableSpace}, > 0
         * otherwise
         */
        public int onTestSize(int suggestedSize, RectF availableSpace);
    }

    public AutoResizeTextView(final Context context) {
        this(context, null, 0);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        // using the minimal recommended font size
        minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        maxTextSize = getTextSize();
        if (maxLines == 0)
            // no value was assigned during construction
            maxLines = NO_LINE_LIMIT;
        // prepare size tester:
        sizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize, final RectF availableSPace) {
                paint.setTextSize(suggestedSize);
                final String text = getText().toString();
                final boolean singleLine = getMaxLines() == 1;
                if (singleLine) {
                    textRect.bottom = paint.getFontSpacing();
                    textRect.right = paint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout(text, paint, widthLimit, Alignment.ALIGN_NORMAL, spacingMult, spacingAdd, true);
                    // return early if we have more lines
                    if (getMaxLines() != NO_LINE_LIMIT && layout.getLineCount() > getMaxLines())
                        return 1;
                    textRect.bottom = layout.getHeight();
                    int maxWidth = -1;
                    for (int i = 0; i < layout.getLineCount(); i++)
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i))
                            maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                    textRect.right = maxWidth;
                }
                textRect.offsetTo(0, 0);
                if (availableSPace.contains(textRect))
                    // may be too small, don't worry we will find the best match
                    return -1;
                // else, too big
                return 1;
            }
        };
        initiallized = true;
    }

    @Override
    public void setTypeface(final Typeface tf) {
        if (paint == null)
            paint = new TextPaint(getPaint());
        paint.setTypeface(tf);
        adjustTextSize();
        super.setTypeface(tf);
    }

    @Override
    public void setTextSize(final float size) {
        maxTextSize = size;
        textCachedSizes.clear();
        adjustTextSize();
    }

    @Override
    public void setMaxLines(final int maxlines) {
        super.setMaxLines(maxlines);
        maxLines = maxlines;
        reAdjust();
    }

    @Override
    public int getMaxLines() {
        return maxLines;
    }

    @Override
    public void setSingleLine() {
        super.setSingleLine();
        maxLines = 1;
        reAdjust();
    }

    @Override
    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine)
            maxLines = 1;
        else maxLines = NO_LINE_LIMIT;
        reAdjust();
    }

    @Override
    public void setLines(final int lines) {
        super.setLines(lines);
        maxLines = lines;
        reAdjust();
    }

    @Override
    public void setTextSize(final int unit, final float size) {
        final Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else r = c.getResources();
        maxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        textCachedSizes.clear();
        adjustTextSize();
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        spacingMult = mult;
        spacingAdd = add;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(final float minTextSize) {
        this.minTextSize = minTextSize;
        reAdjust();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(maxTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextSize);
        }
        super.setText(text, type);
    }

    private void reAdjust() {
        adjustTextSize();
    }

    private void adjustTextSize() {
        // This is a workaround for truncated text issue on ListView, as shown here: https://github.com/AndroidDeveloperLB/AutoFitTextView/pull/14
        // TODO think of a nicer, elegant solution.
        /*if(runnable != null) {
            removeCallbacks(runnable);
        }
        runnable = new Runnable() {
            @Override
            public void run() {*/
                if (!initiallized)
                    return;

                final int startSize = (int) minTextSize;
                final int heightLimit = getMeasuredHeight() - getCompoundPaddingBottom() - getCompoundPaddingTop();
                widthLimit = getMeasuredWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
                if (widthLimit <= 0)
                    return;
                availableSpaceRect.right = widthLimit;
                availableSpaceRect.bottom = heightLimit;
                superSetTextSize(startSize);
            //}
        //};
        //post(runnable);
    }

    private void superSetTextSize(int startSize) {
        int bestSize = efficientTextSizeSearch(startSize, (int) maxTextSize + 1, sizeTester, availableSpaceRect);
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, bestSize);
    }

    /**
     * Enables or disables size caching, enabling it will improve performance
     * where you are animating a value inside TextView. This stores the font
     * size against getText().length() Be careful though while enabling it as 0
     * takes more space than 1 on some fonts and so on.
     *
     * @param enable enable font size caching
     */
    public void setEnableSizeCache(final boolean enable) {
        enableSizeCache = enable;
        textCachedSizes.clear();
        adjustTextSize();
    }

    private int efficientTextSizeSearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        if (!enableSizeCache)
            return binarySearch(start, end, sizeTester, availableSpace);
        final String text = getText().toString();
        final int key = text == null ? 0 : text.length();
        int size = textCachedSizes.get(key);
        if (size != 0)
            return size;
        size = binarySearch(start, end, sizeTester, availableSpace);
        textCachedSizes.put(key, size);
        return size;
    }

    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start;
        int lo = start;
        int hi = end - 1;
        int mid = 0;
        while (lo <= hi) {
            mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableSpace);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
                lastBest = hi;
            } else return mid;
        }
        // make sure to return last best
        // this is what should always be returned
        return lastBest;
    }



    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        if(getLayoutParams() == null || getLayoutParams().width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            reAdjust();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reAdjust();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        textCachedSizes.clear();
    }


}
