package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import org.cryse.novelreader.R;
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
        NovelModel item = getItem(position);

        // 小说标题
        if(viewHolder.mNovelTitleTextView != null) {
            viewHolder.mNovelTitleTextView.setText(item.getTitle());
            if(!mIsShowCoverImage) {
                viewHolder.mNovelTitleTextView.setTextColor(getContext().getResources().getColor(R.color.white_87_percent));
            }
        }

        // 作者 | 状态
        /*StringBuilder breifInfoBuilder = new StringBuilder();
        if (item.getAuthor() != null && !TextUtils.isEmpty(item.getAuthor()))
            breifInfoBuilder.append(item.getAuthor());
        if (item.getStatus() != null && !TextUtils.isEmpty(item.getStatus()))
            breifInfoBuilder.append(" | ").append(item.getStatus());
        viewHolder.mNovelInfo1TextView.setText(breifInfoBuilder.toString());*/

        // 小说作者
        if(viewHolder.mNovelInfo1TextView != null) {
            viewHolder.mNovelInfo1TextView.setText(item.getAuthor());
            if(!mIsShowCoverImage) {
                viewHolder.mNovelInfo1TextView.setTextColor(getContext().getResources().getColor(R.color.white_54_percent));
            }
        }

        // 状态
        if(viewHolder.mNovelInfo2TextView != null && item.getStatus() != null && !TextUtils.isEmpty(item.getStatus())) {
            viewHolder.mNovelInfo2TextView.setText(item.getStatus());
            viewHolder.mNovelInfo2TextView.setVisibility(View.GONE);
        }

        if(viewHolder.mBackCoverLayout != null && !(mIsNightMode && mGrayScale))
            viewHolder.mBackCoverLayout.setBackgroundColor(ColorUtils.getPreDefinedColorFromId(getContext(), item.getId(), item.getTitle().length()));
        
        // 在追人数
        /*if(viewHolder.mNovelInfo2TextView != null && item.getFollow() != null && item.getFollow() != 0)
            viewHolder.mNovelInfo2TextView.setText(getString(R.string.format_follower_count, item.getFollow()));*/

        // 小说封面图片
        if(viewHolder.mNovelImageImageView != null) {
            PicassoHelper.load(getContext(), item.getImageUrl(), viewHolder.mNovelImageImageView, mIsNightMode && mGrayScale);
            /*LetterImageView letterImageView = (LetterImageView)viewHolder.mNovelImageImageView;
            letterImageView.setContent(item.getTitle().charAt(0));
            letterImageView.setBackgroundPainterColor(ColorUtils.getPreDefinedColorFromId(getContext(), item.getId(), item.getTitle().length()));*/
        }
    }
}
