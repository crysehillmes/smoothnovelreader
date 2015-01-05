package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;

import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.List;

public class NovelBookShelfListAdapter extends NovelModelListAdapter {
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray();
    private int mTagColorDotSize;
    private boolean mAllowSelection = false;
    public NovelBookShelfListAdapter(Context context, List<NovelModel> items) {
        super(context, items);
        mTagColorDotSize = UIUtils.dp2px(context, 40f);
    }

    @Override
    public int getLayoutId() {
        return R.layout.listview_item_bookshelf;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        NovelModel item = getItem(position);

        // 小说标题
        viewHolder.mNovelTitleTextView.setText(item.getTitle());

        // 作者 | 类型
        /*StringBuilder breifInfoBuilder = new StringBuilder();
        if (item.getAuthor() != null && !TextUtils.isEmpty(item.getAuthor()))
            breifInfoBuilder.append(item.getAuthor());
        if (item.getCategoryName() != null && !TextUtils.isEmpty(item.getCategoryName()))
            breifInfoBuilder.append(" | ").append(item.getCategoryName());
        viewHolder.mNovelInfo1TextView.setText(breifInfoBuilder.toString());*/

        // 最后阅读
        CharSequence lastReadLabel;
        if (item.getLastReadChapterTitle() != null && !TextUtils.isEmpty(item.getLastReadChapterTitle())) {
            lastReadLabel = getString(R.string.format_last_read_chapter, item.getLastReadChapterTitle());
        } else {
            lastReadLabel = getString(R.string.format_last_read_chapter, getString(R.string.reading_not_start));
        }
        viewHolder.mNovelInfo1TextView.setText(lastReadLabel);

        // 最新章节
        CharSequence latestChapterLabel;
        if (item.getLatestChapterTitle() != null && !TextUtils.isEmpty(item.getLatestChapterTitle())) {
            latestChapterLabel = getString(R.string.format_latest_chapter, item.getLatestChapterTitle());
        } else {
            latestChapterLabel = getString(R.string.format_latest_chapter, getString(R.string.current_none));
        }
        viewHolder.mNovelInfo2TextView.setText(latestChapterLabel);

        // 是否有更新
        if (item.getLatestUpdateCount() > 0) {
            viewHolder.mUnreadTextView.setText("有更新");
        } else {
            viewHolder.mUnreadTextView.setText("");
        }

        if(mSelectedItems.get(position, false)) {
            // 小说封面图片
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRoundRect("\u2713",
                            Color.DKGRAY,
                            mTagColorDotSize
                    ); // radius in px
            viewHolder.mNovelImageImageView.setImageDrawable(textDrawable);
        } else {

            // 小说封面图片
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRoundRect( item.getTitle().length() > 0 ? item.getTitle().substring(0,1) : "",
                            ColorUtils.getSortedPreDefinedColor(getContext(), position),
                            mTagColorDotSize
                    ); // radius in px
            viewHolder.mNovelImageImageView.setImageDrawable(textDrawable);
            //PicassoHelper.load(getContext(), item.getImageUrl(), viewHolder.mNovelImageImageView);
        }
        //viewHolder.mView.setSelected(mSelectedItems.get(position, false));
    }



    public void toggleSelection(int pos) {
        if(!isAllowSelection())
            return;
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        }
        else {
            mSelectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public int[] getSelectedItemPositions() {
        int[] items =
                new int[mSelectedItems.size()];
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items[i] = mSelectedItems.keyAt(i);
        }
        return items;
    }

    public int[] getSelectedItemReversePositions() {
        int size = mSelectedItems.size();
        int[] items =
                new int[size];
        for (int i = 0; i < size; i++) {
            items[size - 1 - i] = mSelectedItems.keyAt(i);
        }
        return items;
    }

    public String[] getSelectedItemIds() {
        String[] items =
                new String[mSelectedItems.size()];
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items[i] = getItem(mSelectedItems.keyAt(i)).getId();
        }
        return items;
    }

    public boolean isAllowSelection() {
        return mAllowSelection;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.mAllowSelection = allowSelection;
        if(allowSelection == false)
            clearSelections();
    }
}
