package org.cryse.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Animation;

public class CircleView extends AbstractBaseView{

    private Animation anim;
    private int circleRadius = 20;
    private int coordinateX = 0;
    private int coordinateY = 0;
    boolean coordinatesLocked = false;
    private int fillColor = -21760;
    boolean fromBottomRight = false;
    private int fromBottomRightValue = 0;
    boolean fromTopRight = false;
    private int fromTopRightValue = 0;
    int height = 0;
    int width = 0;

    public CircleView(Context paramContext)
    {
        super(paramContext);
        init();
    }

    public CircleView(Context paramContext, int paramInt1, int paramInt2)
    {
        super(paramContext);
        this.fillColor = paramInt1;
        this.circleRadius = paramInt2;
        init();
    }

    public CircleView(Context paramContext, AttributeSet paramAttributeSet)
    {
        this(paramContext, paramAttributeSet, 0);
        init();
    }

    public CircleView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private Paint getFill()
    {
        Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        localPaint.setColor(this.fillColor);
        localPaint.setStyle(Paint.Style.FILL);
        return localPaint;
    }

    private void init()
    {
        setMinimumHeight(getHeight());
        setMinimumWidth(getWidth());
        setSaveEnabled(true);
    }

    public void fromBottomRight(int paramInt)
    {
        this.fromBottomRightValue = paramInt;
        this.fromBottomRight = true;
    }

    public void fromTopRight(int paramInt)
    {
        this.fromTopRightValue = paramInt;
        this.fromTopRight = true;
    }

    public int getCoordinateX()
    {
        return this.coordinateX;
    }

    public int getCoordinateY()
    {
        return this.coordinateY;
    }

    public int getHeightVal()
    {
        return this.height;
    }

    public int getWidthVal()
    {
        return this.width;
    }

    protected int hGetMaximumHeight()
    {
        return 2 * this.circleRadius;
    }

    protected int hGetMaximumWidth()
    {
        return 2 * this.circleRadius;
    }

    public void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        if ((this.fromBottomRight) && (!this.coordinatesLocked))
        {
            this.width = getWidth();
            this.height = getHeight();
            this.coordinateX = (this.width - this.circleRadius - this.fromBottomRightValue);
            this.coordinateY = (this.height - this.circleRadius - this.fromBottomRightValue);
            this.coordinatesLocked = true;
        }
        if ((this.fromTopRight) && (!this.coordinatesLocked))
        {
            this.width = getWidth();
            this.height = getHeight();
            this.coordinateX = (this.width - this.circleRadius - this.fromBottomRightValue);
            this.coordinateY = (this.circleRadius + this.fromBottomRightValue);
            this.coordinatesLocked = true;
        }
        paramCanvas.drawCircle(this.coordinateX, this.coordinateY, this.circleRadius, getFill());
    }

    public void setCircleRadius(int paramInt)
    {
        this.circleRadius = paramInt;
    }

    public void setCoordinateX(int paramInt)
    {
        this.coordinateX = paramInt;
    }

    public void setCoordinateY(int paramInt)
    {
        this.coordinateY = paramInt;
    }

    public void setFillColor(int paramInt)
    {
        this.fillColor = paramInt;
    }
}
