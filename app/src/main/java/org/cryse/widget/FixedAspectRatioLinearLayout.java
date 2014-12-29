package org.cryse.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.cryse.novelreader.R;

/**
 * Created by cryse on 10/31/14.
 */
public class FixedAspectRatioLinearLayout extends LinearLayout {
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private int mFixedOrientation = 0;
    private int mWidthWeight = 0;
    private int mHeightWeight = 0;
    private float mAspectRatio = 0.0f;

    public FixedAspectRatioLinearLayout(Context context) {
        super(context);
    }

    public FixedAspectRatioLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FixedAspectRatioLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public FixedAspectRatioLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a =
                context.getTheme().obtainStyledAttributes(attrs, org.cryse.novelreader.R.styleable.FixedAspectRatioAttr, 0, 0);
        mFixedOrientation = a.getInt(R.styleable.FixedAspectRatioAttr_far_fixed_orientation, 0);
        mWidthWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_width_weight, 4);
        mHeightWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_height_weight, 3);
        mAspectRatio = (float) mWidthWeight / mHeightWeight;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        try {


            if (mFixedOrientation == HORIZONTAL) {
                // Image is wider than the display (ratio)
                //int minWidth = MeasureSpec.getSize(widthMeasureSpec);
                //int height = (int) (width / mAspectRatio);
                //width = Math.min(minWidth, width);
                height = (int) (width / mAspectRatio);
            } else {
                // Image is taller than the display (ratio)
                //int minHeight = MeasureSpec.getSize(heightMeasureSpec);
                //int width = (int) (height * mAspectRatio);
                //height = Math.min(height, minHeight);
                width = (int) (height * mAspectRatio);
            }
            setMeasuredDimension(width, height);
        } catch (Exception e) {

        } finally {
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }
}
