package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelChapterListAdapter extends BaseAdapter{
    private Context mContext = null;
    private List<ChapterModel> mContentList = null;
    private LayoutInflater mInflater = null;
    private int mTagColorDotSize;
    private int mTagColorDotPadding;
    private int mCachedColor;
    private int mLastReadPosition = -1;

    public NovelChapterListAdapter(Context context,
                                   List<ChapterModel> novelContents) {
        this.mContext = context;
        this.mContentList = novelContents;
        mInflater = LayoutInflater.from(this.mContext);
        mTagColorDotSize = UIUtils.dp2px(mContext, 12f);
        mTagColorDotPadding = UIUtils.dp2px(mContext, 4f);
        mCachedColor = ColorUtils.getColorFromAttr(context, R.attr.colorPrimary);
    }

    @Override
    public int getCount() {
        return mContentList.size();
    }

    @Override
    public ChapterModel getItem(int position) {
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        NovelIntroItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_novel_chapter, null);
            viewHolder = new NovelIntroItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (NovelIntroItemViewHolder)convertView.getTag();
        }
        ChapterModel item = mContentList.get(position);

        ShapeDrawable colorDrawable = new ShapeDrawable(new OvalShape());
        colorDrawable.setIntrinsicWidth(mTagColorDotSize);
        colorDrawable.setIntrinsicHeight(mTagColorDotSize);
        colorDrawable.getPaint().setStyle(Paint.Style.FILL);
        viewHolder.mNovelChapterTitleTextView.setCompoundDrawablesWithIntrinsicBounds(colorDrawable,
                null, null, null);
        viewHolder.mNovelChapterTitleTextView.setCompoundDrawablePadding(mTagColorDotPadding);
        colorDrawable.getPaint().setColor(
                item.isCached() ?
                        mCachedColor :
                        Color.TRANSPARENT
        );
        if(mLastReadPosition == position)
        {
            Spannable chapterTitle = new SpannableString(item.getTitle());
            viewHolder.mNovelChapterTitleTextView.setText(chapterTitle);

            Spannable lastReadPromptText = new SpannableString(getContext().getResources().getString(R.string.listview_last_read_indicator_text));
            lastReadPromptText.setSpan(new ForegroundColorSpan(ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent)), 0, lastReadPromptText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.mNovelChapterTitleTextView.append(lastReadPromptText);
        } else {
            viewHolder.mNovelChapterTitleTextView.setText(item.getTitle());
        }

        return convertView;
    }

    public int getLastReadIndicator() {
        return mLastReadPosition;
    }

    public void setLastReadIndicator(int position) {
        mLastReadPosition = position;
    }

    public void clearLastReadIndicator() {
        mLastReadPosition = -1;
    }

    public Context getContext() {
        return mContext;
    }

    public class NovelIntroItemViewHolder
    {
        public View view;
        @InjectView(R.id.listview_item_novel_chapters_item_chapter_title)
        public TextView mNovelChapterTitleTextView;

        public NovelIntroItemViewHolder(View view) {
            this.view = view;
            ButterKnife.inject(this, view);
        }
    }
}
