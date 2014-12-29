package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelChapterModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelChapterListAdapter extends BaseAdapter{
    private Context mContext = null;
    private List<NovelChapterModel> mContentList = null;
    private LayoutInflater mInflater = null;
    private Handler mHandler;
    public View getFirstItemView() {
        return mFirstItemView;
    }

    private View mFirstItemView = null;
    public int getmGroupSize() {
        return mGroupSize;
    }

    public void setmGroupSize(int mGroupSize) {
        this.mGroupSize = mGroupSize;
    }

    private int mGroupSize = 10;
    public NovelChapterListAdapter(Context context,
                                   List<NovelChapterModel> novelContents) {
        this.mContext = context;
        this.mContentList = novelContents;
        mInflater = LayoutInflater.from(this.mContext);
        mHandler = new Handler();
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
            if(position == 0) {
                mFirstItemView = convertView;
            }
        }
        else
        {
            viewHolder = (NovelIntroItemViewHolder)convertView.getTag();
        }
        NovelChapterModel item = mContentList.get(position);

        viewHolder.mNovelChapterTitleTextView.setText(item.getTitle());
        //TextPaint tp = viewHolder.mNovelTitleTextView .getPaint();
        //tp.setFakeBoldText(true);

        return convertView;
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
