package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.widget.ReadWidgetAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadViewFlipAdapter extends BaseAdapter implements ReadWidgetAdapter {
    private Context mContext = null;
    private LayoutInflater inflater = null;
    private ArrayList<CharSequence> mContentList = new ArrayList<CharSequence>();
    private float mFontSize;
    private int mBackgroundColor;
    public ReadViewFlipAdapter(
            Context context,
            float fontSize,
            int backgroundColor
    ) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(this.mContext);
        this.mFontSize = fontSize;
        this.mBackgroundColor = backgroundColor;
    }

    public int getPageFromStringOffset(int offset) {
        int length = 0;
        for(int i = 0; i < getCount(); i++ ) {
            length += mContentList.get(i).length();
            if(length > offset)
                return i;
        }
        return 0;
    }

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
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FlipNovelChapterViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_chapter_content_textview, null);
            viewHolder = new FlipNovelChapterViewHolder();
            viewHolder.mNovelChapterTextView = (TextView) convertView.findViewById(R.id.layout_chapter_content_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FlipNovelChapterViewHolder) convertView.getTag();
        }
        final TextView readTextView = viewHolder.mNovelChapterTextView;
        readTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
        readTextView.setText(mContentList.get(position));
        readTextView.setLineSpacing(0f, 1.3f);
        readTextView.setBackgroundColor(mBackgroundColor);
        return convertView;
    }

    public class FlipNovelChapterViewHolder {
        public TextView mNovelChapterTextView;
    }

    @Override
    public void replaceContent(List<CharSequence> newContents) {
        if(this.mContentList != null) {
            this.mContentList.clear();
            this.mContentList.addAll(newContents);
        }
        notifyDataSetChanged();
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        notifyDataSetChanged();
    }

    @Override
    public void setFontSize(float fontSize) {
        this.mFontSize = fontSize;
    }

    @Override
    public ArrayList<CharSequence> getContent() {
        return mContentList;
    }
}
