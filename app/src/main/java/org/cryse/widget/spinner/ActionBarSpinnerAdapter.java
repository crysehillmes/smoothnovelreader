package org.cryse.widget.spinner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.novelreader.R;

import java.util.ArrayList;

public class ActionBarSpinnerAdapter extends BaseAdapter {
    private int mDotSize;
    private boolean mTopLevel;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Handler mHandler;
    public ActionBarSpinnerAdapter(Context context, boolean topLevel) {
        this.mTopLevel = topLevel;
        mContext = context;
        if(context instanceof Activity) {
            mLayoutInflater = ((Activity)context).getLayoutInflater();
        } else {
            mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mHandler = new Handler();
    }

    // pairs of (tag, title)
    private ArrayList<ActionBarSpinnerItem> mItems = new ArrayList<ActionBarSpinnerItem>();

    public void clear() {
        mItems.clear();
    }

    public void addItem(String title, String value, int color) {
        mItems.add(new ActionBarSpinnerItem(title, value, false, color));
    }

    public void addHeader(String title) {
        mItems.add(new ActionBarSpinnerItem(title, "", true, 0));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ActionBarSpinnerItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isHeader(int position) {
        return position >= 0 && position < mItems.size()
                && mItems.get(position).isHeader();
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = mLayoutInflater.inflate(R.layout.layout_spinner_item_dropdown,
                    parent, false);
            view.setTag("DROPDOWN");
        }

        TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
        View dividerView = view.findViewById(R.id.divider_view);
        TextView normalTextView = (TextView) view.findViewById(R.id.normal_text);

        if (isHeader(position)) {
            headerTextView.setText(getTitle(position));
            headerTextView.setVisibility(View.VISIBLE);
            normalTextView.setVisibility(View.GONE);
            dividerView.setVisibility(View.VISIBLE);
        } else {
            headerTextView.setVisibility(View.GONE);
            normalTextView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.GONE);

            setUpNormalDropdownView(position, normalTextView);
        }
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mLayoutInflater.inflate(mTopLevel
                            ? R.layout.layout_spinner_item_actionbar
                            : R.layout.layout_spinner_item,
                    parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getTitle() : "";
    }

    private int getColor(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getColor() : 0;
    }

    private String getValue(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getValue() : "";
    }

    private void setUpNormalDropdownView(int position, TextView textView) {
        textView.setText(getTitle(position));
        ShapeDrawable colorDrawable = (ShapeDrawable) textView.getCompoundDrawables()[2];
        int color = getColor(position);
        if (color == 0) {
            if (colorDrawable != null) {
                textView.setCompoundDrawables(null, null, null, null);
            }
        } else {
            if (mDotSize == 0) {
                mDotSize = mContext.getResources().getDimensionPixelSize(
                        R.dimen.tag_color_dot_size);
            }
            if (colorDrawable == null) {
                colorDrawable = new ShapeDrawable(new OvalShape());
                colorDrawable.setIntrinsicWidth(mDotSize);
                colorDrawable.setIntrinsicHeight(mDotSize);
                colorDrawable.getPaint().setStyle(Paint.Style.FILL);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, colorDrawable, null);
            }
            colorDrawable.getPaint().setColor(color);
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}