package org.cryse.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import me.zhanghai.android.materialprogressbar.IndeterminateProgressDrawable;

public class MaterialProgressBar extends ProgressBar {
    public MaterialProgressBar(Context context) {
        super(context);
        setDefaultIndeterminateDrawable();
    }

    public MaterialProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultIndeterminateDrawable();
    }

    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultIndeterminateDrawable();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setDefaultIndeterminateDrawable();
    }

    private void setDefaultIndeterminateDrawable() {
        this.setIndeterminateDrawable(new IndeterminateProgressDrawable(getContext()));
    }
}
