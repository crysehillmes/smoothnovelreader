package org.cryse.novelreader.util.navidrawer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.util.ColorUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerItemView extends RelativeLayout {
    private int mPosition;
    private Context mContext;
    @InjectView(R.id.itemRR) RelativeLayout rr;

    @InjectView(R.id.title)
    TextView itemTitleTV;

    @InjectView(R.id.icon)
    ImageView itemIconIV;

    final Resources res;
    int selectionColor;
    int transparentColor;

    int textViewDefalutColor;
    int iconDefaultColor;

    public NavigationDrawerItemView(Context context) {
        super(context);
        mContext = context;
        res = context.getResources();
        init();
    }

    public NavigationDrawerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        res = context.getResources();
        init();
    }

    public NavigationDrawerItemView(Context context, AttributeSet attrs,
                                    int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        res = context.getResources();
        init();
    }

    private void init() {
        selectionColor = ColorUtils.getColorFromAttr(getContext(), R.attr.colorPrimary);
        transparentColor = res.getColor(android.R.color.transparent);

        int[] items = new int[]{R.attr.smooth_theme_navigation_drawer_text_color, R.attr.smooth_theme_navigation_drawer_icon_color};
        TypedArray typedArray = mContext.obtainStyledAttributes(items);
        textViewDefalutColor = typedArray.getColor(0, Color.BLACK);
        iconDefaultColor = typedArray.getColor(1, Color.LTGRAY);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }

    public void bindTo(NavigationDrawerItem item, int position) {
        this.mPosition = position;
        if (item.isMainItem()) {
            itemTitleTV.setText(item.getItemName());
            itemIconIV.setImageDrawable(getIcon(item.getItemIcon()));
            itemIconIV.setVisibility(View.VISIBLE);
        } else {
            itemTitleTV.setText(item.getItemName());
            itemIconIV.setImageDrawable(getIcon(item.getItemIcon()));
            itemIconIV.setVisibility(View.VISIBLE);
        }

        if(item.isSelected()) {
            itemIconIV.getDrawable().setColorFilter(selectionColor, PorterDuff.Mode.SRC_IN);
            itemTitleTV.setTextColor(selectionColor);
        } else {
            itemIconIV.getDrawable().setColorFilter(iconDefaultColor, PorterDuff.Mode.SRC_IN);
            itemTitleTV.setTextColor(textViewDefalutColor);
        }

    }

    private Drawable getIcon(int res) {
        return getContext().getResources().getDrawable(res);
    }
}
