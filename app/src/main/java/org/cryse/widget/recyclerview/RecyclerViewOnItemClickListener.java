package org.cryse.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface RecyclerViewOnItemClickListener {
    public void onItemClick(View view, int position, long id);
}