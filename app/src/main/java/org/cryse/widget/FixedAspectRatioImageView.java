package org.cryse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.cryse.novelreader.R;

public class FixedAspectRatioImageView extends ImageView {
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private int mFixedOrientation = 0;
    private int mWidthWeight = 0;
    private int mHeightWeight = 0;
    private float mAspectRatio = 0.0f;

    public FixedAspectRatioImageView(Context context) {
        super(context);
    }

    public FixedAspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a =
                context.getTheme().obtainStyledAttributes(attrs, org.cryse.novelreader.R.styleable.FixedAspectRatioAttr, 0, 0);
        mFixedOrientation = a.getInt(R.styleable.FixedAspectRatioAttr_far_fixed_orientation, 0);
        mWidthWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_width_weight, 4);
        mHeightWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_height_weight, 3);
        mAspectRatio = (float) mWidthWeight / mHeightWeight;
        a.recycle();
    }

    public FixedAspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray
                a =
                context.getTheme().obtainStyledAttributes(attrs, org.cryse.novelreader.R.styleable.FixedAspectRatioAttr, 0, 0);
        mFixedOrientation = a.getInt(R.styleable.FixedAspectRatioAttr_far_fixed_orientation, 0);
        mWidthWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_width_weight, 4);
        mHeightWeight = a.getInt(R.styleable.FixedAspectRatioAttr_far_height_weight, 3);
        mAspectRatio = (float) mWidthWeight / (float)mHeightWeight;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            Drawable drawable = getDrawable();
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            if (drawable == null) {
                setMeasuredDimension(0, 0);
            } else {
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
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}