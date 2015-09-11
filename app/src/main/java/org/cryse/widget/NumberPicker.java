package org.cryse.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.cryse.novelreader.R;

public class NumberPicker extends ViewGroup {
    private static final float DEFAULT_BUTTON_WIDTH = 48f;
    private int mButtonWidth = 0;
    private int mTextColor;
    private float mTextSize;
    private int mButtonIconColor;
    private int mMinValue;
    private int mMaxValue;
    private int mStep;
    private String mValueDisplayPrefix;
    private String mValueDisplaySuffix;
    private Drawable mDecreaseDrawable;
    private Drawable mIncreaseDrawable;
    private OnNumberPickedListener mOnNumberPickedListener;

    private TextView mValueTextView;
    private ImageButton mIncreaseButton;
    private ImageButton mDecreaseButton;

    private int mCurrentValue;

    public NumberPicker(Context context) {
        super(context);
        init(null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static float sp2px(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    public static int getColorFromAttr(Context context, int attr) {
        int[] textSizeAttr = new int[]{attr};
        TypedArray a = context.obtainStyledAttributes(textSizeAttr);
        int color = a.getColor(0, Color.RED);
        a.recycle();
        return color;
    }

    private void init(AttributeSet attrs) {
        setSaveEnabled(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_number_picker, this, true);

        mDecreaseButton = (ImageButton) findViewById(R.id.imagebutton_decrease);
        mValueTextView = (TextView) findViewById(R.id.textview_value);
        mIncreaseButton = (ImageButton) findViewById(R.id.imagebutton_increase);

        if (attrs != null) {
            TypedArray attrsValue = getContext().obtainStyledAttributes(attrs,
                    R.styleable.NumberPicker);
            mMaxValue = attrsValue.getInt(R.styleable.NumberPicker_np_max, 0);
            mMinValue = attrsValue.getInt(R.styleable.NumberPicker_np_min, 0);
            mStep = attrsValue.getInt(R.styleable.NumberPicker_np_step, 0);
            mTextColor = attrsValue.getColor(R.styleable.NumberPicker_np_textColor, 0);
            mTextSize = attrsValue.getDimension(R.styleable.NumberPicker_np_textSize, sp2px(getContext(), 14f));
            mButtonIconColor = attrsValue.getColor(R.styleable.NumberPicker_np_buttonWidth, getColorFromAttr(getContext(), android.R.attr.textColorPrimary));
            mButtonWidth = attrsValue.getDimensionPixelSize(R.styleable.NumberPicker_np_buttonWidth, dp2px(getContext(), DEFAULT_BUTTON_WIDTH));
            mIncreaseDrawable = attrsValue.getDrawable(R.styleable.NumberPicker_np_buttonIncreaseIcon);
            mDecreaseDrawable = attrsValue.getDrawable(R.styleable.NumberPicker_np_buttonDecreaseIcon);
            attrsValue.recycle();
        }

        if (mDecreaseDrawable != null) {
            mDecreaseDrawable.mutate().setColorFilter(new PorterDuffColorFilter(mButtonIconColor, PorterDuff.Mode.SRC_IN));
            mDecreaseButton.setImageDrawable(mDecreaseDrawable);
        }
        if (mIncreaseDrawable != null) {
            mIncreaseDrawable.mutate().setColorFilter(new PorterDuffColorFilter(mButtonIconColor, PorterDuff.Mode.SRC_IN));
            mIncreaseButton.setImageDrawable(mIncreaseDrawable);
        }
        mValueTextView.setTextColor(mTextColor);
        mValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        updateValueTextView();
        mDecreaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentValue >= mMinValue && mCurrentValue - mStep >= mMinValue) {
                    mCurrentValue -= mStep;
                } else {
                    mCurrentValue = mMinValue;
                }
                updateValueTextView();
                if (mOnNumberPickedListener != null) {
                    mOnNumberPickedListener.onNumberPicked(mCurrentValue);
                }
            }
        });
        mIncreaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentValue <= mMaxValue && mCurrentValue + mStep <= mMaxValue) {
                    mCurrentValue += mStep;
                } else {
                    mCurrentValue = mMaxValue;
                }
                updateValueTextView();
                if (mOnNumberPickedListener != null) {
                    mOnNumberPickedListener.onNumberPicked(mCurrentValue);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int textViewWidth = widthSize - 2 * mButtonWidth;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (i == 0 || i == 2) {
                    int buttonChildWidthSpec = MeasureSpec.makeMeasureSpec(mButtonWidth, MeasureSpec.EXACTLY);
                    measureChild(child, buttonChildWidthSpec, heightMeasureSpec);
                } else {
                    int textChildWidthSpec = MeasureSpec.makeMeasureSpec(textViewWidth, MeasureSpec.EXACTLY);
                    measureChild(child, textChildWidthSpec, heightMeasureSpec);
                }
            }
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int viewWidth = r - l;
        int viewHeight = b - t;
        int leftStart = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (i == 0) {
                int left = 0;
                int top = 0;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                leftStart += right - left;
            } else if (i == 1) {
                int left = leftStart;
                int top = 0;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
                leftStart += right - left;
            }
            if (i == 2) {
                int left = leftStart;
                int top = 0;
                int right = left + child.getMeasuredWidth();
                int bottom = top + child.getMeasuredHeight();
                child.layout(left, top, right, bottom);
            }
        }
    }

    private void updateValueTextView() {
        String valueString = (TextUtils.isEmpty(mValueDisplayPrefix) ? "" : mValueDisplayPrefix) +
                Integer.toString(mCurrentValue) +
                (TextUtils.isEmpty(mValueDisplaySuffix) ? "" : mValueDisplaySuffix);
        mValueTextView.setText(valueString);
    }

    public int getMinValue() {
        return mMinValue;
    }

    public void setMinValue(int minValue) {
        this.mMinValue = minValue;
        if (mCurrentValue < mMinValue) {
            mCurrentValue = mMinValue;
            updateValueTextView();
        }
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        this.mMaxValue = maxValue;
        if (mCurrentValue > mMaxValue) {
            mCurrentValue = mMaxValue;
            updateValueTextView();
        }
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        this.mStep = step;
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.mCurrentValue = currentValue;
        updateValueTextView();
    }

    public void setOnNumberPickedListener(OnNumberPickedListener onNumberPickedListener) {
        this.mOnNumberPickedListener = onNumberPickedListener;
    }

    public String getValueDisplaySuffix() {
        return mValueDisplaySuffix;
    }

    public void setValueDisplaySuffix(String valueDisplaySuffix) {
        this.mValueDisplaySuffix = valueDisplaySuffix;
    }

    public String getValueDisplayPrefix() {
        return mValueDisplayPrefix;
    }

    public void setValueDisplayPrefix(String valueDisplayPrefix) {
        this.mValueDisplayPrefix = valueDisplayPrefix;
    }

    public interface OnNumberPickedListener {
        void onNumberPicked(int number);
    }
}
