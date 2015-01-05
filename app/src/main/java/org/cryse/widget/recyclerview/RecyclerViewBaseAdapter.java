package org.cryse.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class RecyclerViewBaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected RecyclerViewOnItemClickListener mOnItemClickListener;
    protected RecyclerViewOnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        if(holder.itemView != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, position, getItemId(position));
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnItemLongClickListener != null) {
                        return mOnItemLongClickListener.onItemLongClick(view, position, getItemId(position));
                    }
                    return false;
                }
            });
        }
    }
}
