package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.adapter.item.NovelCategoryItem;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class NovelCategoryItemAdapter extends RecyclerView.Adapter<NovelCategoryItemAdapter.NovelCategoryItemViewHolder> {
    private List<NovelCategoryItem> mItems = new ArrayList<NovelCategoryItem>();
    private Context mContext;
    public NovelCategoryItemAdapter(Context context) {
        mContext = context;
        mTagColorDotSize = UIUtils.dp2px(mContext, 16f);
    }

    private int mTagColorDotSize;
    public static final int LAST_POSITION = -1 ;

    public void addAll(List<NovelCategoryItem> items) {
        addAll(LAST_POSITION, items);
    }

    public void addAll(int position, List<NovelCategoryItem> items) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mItems.addAll(position, items);

        notifyItemRangeInserted(position, items.size());
    }

    public void add(NovelCategoryItem item) {
        add(LAST_POSITION, item);
    }

    public void add(int position, NovelCategoryItem item) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position == LAST_POSITION && getItemCount() > 0)
            position = getItemCount() - 1;

        if (position > LAST_POSITION && position < getItemCount()) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public NovelCategoryItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_category, viewGroup, false);
        return new NovelCategoryItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NovelCategoryItemViewHolder viewHolder, int position) {
        NovelCategoryItem item = getItem(position);
        if (viewHolder.imageView != null) {
            if(item.getIcon() == 0) {
                TextDrawable textDrawable = TextDrawable.builder()
                        .buildRoundRect( item.getTitle().length() > 0 ? item.getTitle().substring(0,1) : "",
                                ColorUtils.getSortedPreDefinedColor(mContext, position),
                                mTagColorDotSize
                        ); // radius in px
                viewHolder.imageView.setImageDrawable(textDrawable);
            } else {
                viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(item.getIcon()));
                viewHolder.imageView.getDrawable().setColorFilter(
                        ColorUtils.getSortedPreDefinedColor(mContext, position),
                        PorterDuff.Mode.SRC_IN
                );
            }
        }

        viewHolder.textView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public NovelCategoryItem getItem(int position) {
        return mItems.get(position);
    }

    public static class NovelCategoryItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ViewGroup bgContainer;
        public NovelCategoryItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.icon);
            textView = (TextView)itemView.findViewById(R.id.title);
            bgContainer = (ViewGroup)itemView.findViewById(R.id.category_item_container);
        }
    }
}