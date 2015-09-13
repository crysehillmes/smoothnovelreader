package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.PicassoHelper;

import java.util.List;

public class NovelOnlineListAdapter extends NovelModelListAdapter {
    private boolean mIsShowCoverImage = true;
    private boolean mIsNightMode = false;
    private boolean mGrayScale = false;
    public NovelOnlineListAdapter(Context context, List<NovelModel> novelList, boolean isShowCoverImage, boolean isNightMode, boolean grayScale) {
        super(context, novelList);
        mIsShowCoverImage = isShowCoverImage;
        mIsNightMode = isNightMode;
        mGrayScale = grayScale;
    }

    @Override
    public int getLayoutId() {
        if(mIsShowCoverImage)
            return R.layout.listview_item_online_novel_with_coverimg;
        else
            return R.layout.listview_item_online_novel;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        NovelModel item = getItem(position);
        Novel novel = null;
        if (item instanceof Novel) {
            novel = (Novel) item;
        }
        if(viewHolder.mNovelTitleTextView != null) {
            viewHolder.mNovelTitleTextView.setText(item.getTitle());
            if(!mIsShowCoverImage) {
                viewHolder.mNovelTitleTextView.setTextColor(getContext().getResources().getColor(R.color.white_87_percent));
            }
        }

        if(viewHolder.mNovelInfo1TextView != null) {
            viewHolder.mNovelInfo1TextView.setText(item.getAuthor());
            if(!mIsShowCoverImage) {
                viewHolder.mNovelInfo1TextView.setTextColor(getContext().getResources().getColor(R.color.white_54_percent, null));
            }
        }

        if (viewHolder.mNovelInfo2TextView != null && novel != null && !TextUtils.isEmpty(novel.getCategory())) {
            viewHolder.mNovelInfo2TextView.setText(novel.getSummary());
            viewHolder.mNovelInfo2TextView.setVisibility(View.VISIBLE);
        }

        if(viewHolder.mBackCoverLayout != null && !(mIsNightMode && mGrayScale))
            viewHolder.mBackCoverLayout.setBackgroundColor(ColorUtils.getPreDefinedColorFromId(getContext().getResources(), item.getNovelId(), item.getTitle().length()));

        if(viewHolder.mNovelImageImageView != null) {
            PicassoHelper.load(getContext(), item.getCoverImage(), viewHolder.mNovelImageImageView, mIsNightMode && mGrayScale);
        }
    }
}
