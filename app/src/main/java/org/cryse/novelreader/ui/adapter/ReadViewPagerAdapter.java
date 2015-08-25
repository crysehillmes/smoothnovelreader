package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.widget.ReadWidgetAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadViewPagerAdapter extends PagerAdapter implements ReadWidgetAdapter {
    private ArrayList<CharSequence> mContentList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private float mFontSize;
    private int mBackgroundColor;
    private float mLineSpacingMultiplier;

    public ReadViewPagerAdapter(Context context, float fontSize, float lineSpacingMultiplier, int backgroundColor) {
        this.mContext = context;
        this.mFontSize = fontSize;
        this.mLineSpacingMultiplier = lineSpacingMultiplier;
        this.mContentList = new ArrayList<CharSequence>();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mBackgroundColor = backgroundColor;
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == ((View) o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.layout_chapter_content_textview, null);
        container.addView(view);
        TextView textView = (TextView)view;
        textView.setText(mContentList.get(position));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
        textView.setLineSpacing(0f, mLineSpacingMultiplier);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        //super.destroyItem(container, position, object);
    }

    @Override
    public void replaceContent(List<CharSequence> newContents) {
        mContentList.clear();
        notifyDataSetChanged();
        mContentList.addAll(newContents);
        notifyDataSetChanged();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    @Override
    public void setFontSize(float fontSize) {
        this.mFontSize = fontSize;
    }

    @Override
    public void setLineSpacing(float lineSpacingMultiplier) {
        this.mLineSpacingMultiplier = lineSpacingMultiplier;
    }

    @Override
    public int getPageFromStringOffset(int offset) {
        int length = 0;
        for(int i = 0; i < getCount(); i++ ) {
            length += mContentList.get(i).length();
            if(length > offset)
                return i;
        }
        return 0;
    }

    @Override
    public int getStringOffsetFromPage(int page) {
        int length = 0;
        if(page > mContentList.size())
            throw new IllegalArgumentException("Out of bound");
        for(int i = 0; i < page; i++ ) {
            length += mContentList.get(i).length();
        }
        return length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public ArrayList<CharSequence> getContent() {
        return mContentList;
    }
}
