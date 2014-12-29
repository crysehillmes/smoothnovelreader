package org.cryse.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractBaseView extends View {
    public AbstractBaseView(Context paramContext)
    {
        super(paramContext);
    }

    public AbstractBaseView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public AbstractBaseView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private int getImprovedDefaultHeight(int paramInt)
    {
        int i = View.MeasureSpec.getMode(paramInt);
        int j = View.MeasureSpec.getSize(paramInt);
        switch (i)
        {
            case 1073741824:
            default:
                return j;
            case 0:
                return hGetMaximumHeight();
            case -2147483648:
        }
        return hGetMinimumHeight();
    }

    private int getImprovedDefaultWidth(int paramInt)
    {
        int i = View.MeasureSpec.getMode(paramInt);
        int j = View.MeasureSpec.getSize(paramInt);
        switch (i)
        {
            case MeasureSpec.EXACTLY:
            default:
                return j;
            case MeasureSpec.UNSPECIFIED:
                return hGetMaximumWidth();
            case MeasureSpec.AT_MOST:
        }
        return hGetMinimumWidth();
    }

    protected abstract int hGetMaximumHeight();

    protected abstract int hGetMaximumWidth();

    protected int hGetMinimumHeight()
    {
        return getSuggestedMinimumHeight();
    }

    protected int hGetMinimumWidth()
    {
        return getSuggestedMinimumWidth();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(getImprovedDefaultWidth(widthMeasureSpec), getImprovedDefaultHeight(heightMeasureSpec));
    }
}