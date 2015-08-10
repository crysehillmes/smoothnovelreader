package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
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
    private int mLastReadIconSize;
    private int mTagColorDotPadding;
    private int mCachedColor;
    private int mLastReadPosition = -1;
    private int mColorAccent = 0;

    public NovelChapterListAdapter(Context context,
                                   List<ChapterModel> novelContents) {
        this.mContext = context;
        this.mContentList = novelContents;
        mInflater = LayoutInflater.from(this.mContext);
        mTagColorDotSize = UIUtils.dp2px(mContext, 12f);
        mTagColorDotPadding = UIUtils.dp2px(mContext, 4f);
        mLastReadIconSize = UIUtils.dp2px(mContext, 24f);
        if(context instanceof AbstractThemeableActivity) {
            mCachedColor = ((AbstractThemeableActivity) context).getThemeEngine().getPrimaryColor(context);
        } else {
            mCachedColor = ColorUtils.getColorFromAttr(context, R.attr.colorPrimary);
        }
        mColorAccent = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
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

        if(mLastReadPosition != position) {
            ShapeDrawable colorDrawable = new ShapeDrawable(new OvalShape());
            int drawPaddingLeft = (mLastReadIconSize - mTagColorDotSize) / 2;
            colorDrawable.setIntrinsicWidth(mTagColorDotSize);
            colorDrawable.setIntrinsicHeight(mTagColorDotSize);
            colorDrawable.setBounds(drawPaddingLeft, 0, mTagColorDotSize + drawPaddingLeft, mTagColorDotSize);
            colorDrawable.getPaint().setStyle(Paint.Style.FILL);
            viewHolder.mNovelChapterTitleTextView.setCompoundDrawables(colorDrawable,
                    null, null, null);
            viewHolder.mNovelChapterTitleTextView.setCompoundDrawablePadding(mTagColorDotPadding + (mLastReadIconSize - mTagColorDotSize));
            colorDrawable.getPaint().setColor(
                    item.isCached() ?
                            mCachedColor :
                            Color.TRANSPARENT
            );
        } else {
            Drawable lastReadDrawable = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_action_history, null);
            lastReadDrawable.mutate().setColorFilter(mColorAccent, PorterDuff.Mode.SRC_IN);
            lastReadDrawable.setBounds(0, 0, mLastReadIconSize, mLastReadIconSize);
            viewHolder.mNovelChapterTitleTextView.setCompoundDrawables(lastReadDrawable,
                    null, null, null);
            viewHolder.mNovelChapterTitleTextView.setCompoundDrawablePadding(mTagColorDotPadding);

        }

        viewHolder.mNovelChapterTitleTextView.setText(item.getTitle());

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
