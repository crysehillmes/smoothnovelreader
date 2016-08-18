package org.cryse.novelreader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.adapter.item.NovelDetailItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NovelDetailAdapter extends RecyclerView.Adapter<NovelDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<NovelDetailItem> mItemList;

    public NovelDetailAdapter(Context context, List<NovelDetailItem> items) {
        this.mContext = context;
        if (items == null) {
            this.mItemList = new ArrayList<>();
        } else {
            this.mItemList = items;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            default:
            case NovelDetailItem.TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_detail_item, parent, false);
                return new ItemViewHolder(view);
            case NovelDetailItem.TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_detail_header, parent, false);
                return new HeaderViewHolder(view);
            case NovelDetailItem.TYPE_DIVIDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_item_detail_divider, parent, false);
                return new DividerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NovelDetailItem item = mItemList.get(position);
        if (holder instanceof ItemViewHolder) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(item.getText());
            ((ItemViewHolder) holder).mTextView.setText(builder);
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).mTextView.setText(item.getText());
        } else if (holder instanceof DividerViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getType();
        // return super.getItemViewType(position);
    }

    public void clear() {
        int itemCount = mItemList.size();
        mItemList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }


    public void addAll(Collection<NovelDetailItem> items) {
        int currentItemCount = mItemList.size();
        mItemList.addAll(items);
        notifyItemRangeInserted(currentItemCount, items.size());
    }

    public void addAll(int position, Collection<NovelDetailItem> items) {
        int currentItemCount = mItemList.size();
        if (position > currentItemCount)
            throw new IndexOutOfBoundsException();
        else
            mItemList.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void replaceWith(Collection<NovelDetailItem> items) {
        replaceWith(items, false);
    }

    public void replaceWith(Collection<NovelDetailItem> items, boolean cleanToReplace) {
        if (cleanToReplace) {
            clear();
            addAll(items);
        } else {
            int oldCount = mItemList.size();
            int newCount = items.size();
            int delCount = oldCount - newCount;
            mItemList.clear();
            mItemList.addAll(items);
            if (delCount > 0) {
                notifyItemRangeChanged(0, newCount);
                notifyItemRangeRemoved(newCount, delCount);
            } else if (delCount < 0) {
                notifyItemRangeChanged(0, oldCount);
                notifyItemRangeInserted(oldCount, -delCount);
            } else {
                notifyItemRangeChanged(0, newCount);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ItemViewHolder extends ViewHolder {
        @Bind(android.R.id.text1)
        TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class HeaderViewHolder extends ViewHolder {
        @Bind(android.R.id.text1)
        TextView mTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class DividerViewHolder extends ViewHolder {

        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
