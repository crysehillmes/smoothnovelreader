package org.cryse.novelreader.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public abstract class NovelModelListAdapter extends RecyclerViewBaseAdapter<NovelModelListAdapter.ViewHolder>
    implements ContentAdapter<NovelModel>{
    public static final int LAST_POSITION = -1 ;

    private Context mContext;
    private Resources mResouces;
    private List<NovelModel> mNovelList;
    private LayoutInflater mLayoutInflater;
    private Handler mHandler;
    private String mCurrentCategory;


    public NovelModelListAdapter(Context context, List<NovelModel> novelList) {
        mContext = context;
        mResouces = context.getResources();
        mNovelList = novelList;
        if(context instanceof Activity) {
            mLayoutInflater = ((Activity)context).getLayoutInflater();
        } else {
            mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mHandler = new Handler();
    }

    public void replaceWith(List<NovelModel> items) {
        this.mNovelList.clear();
        mNovelList.addAll(items);
        notifyItemRangeChanged(0, items.size());
    }

    public void addAll(List<NovelModel> novelModels) {
        addAll(LAST_POSITION, novelModels);
    }

    public void addAll(int position, List<NovelModel> novelModels) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mNovelList.addAll(position, novelModels);

        notifyItemRangeInserted(position, novelModels.size());
    }

    public void add(NovelModel novelModel) {
        add(LAST_POSITION, novelModel);
    }

    public void add(int position, NovelModel novelModel) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mNovelList.add(position, novelModel);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position == LAST_POSITION && getItemCount() > 0)
            position = getItemCount() - 1;

        if (position > LAST_POSITION && position < getItemCount()) {
            mNovelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return mNovelList.size();
    }

    public NovelModel getItem(int position) {
        return mNovelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public NovelModelListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(getLayoutId(), parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        @InjectView(R.id.listview_item_novel_title_textview)
        public TextView mNovelTitleTextView;
        @InjectView(R.id.listview_item_novel_info1_textview)
        public TextView mNovelInfo1TextView;
        @Optional
        @InjectView(R.id.listview_item_novel_info2_textview)
        public TextView mNovelInfo2TextView;
        /*@InjectView(R.id.listview_item_novel_info3_textview)
        public TextView mNovelInfo3TextView;*/
        @Optional
        @InjectView(R.id.listview_item_novel_image_imageview)
        public ImageView mNovelImageImageView;

        @Optional
        @InjectView(R.id.listview_item_novel_bg_view)
        public LinearLayout mBackCoverLayout;

        @Optional
        @InjectView(R.id.listview_item_novel_unread_textview)
        public TextView mUnreadTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    public String getCurrentCategory() {
        return mCurrentCategory;
    }

    public String getString(int resId) {
        return mResouces.getString(resId);
    }

    public String getString(int resId, Object... args) {
        return mResouces.getString(resId,args);
    }

    public Context getContext() {
        return mContext;
    }

    public List<NovelModel> getNovelList() {
        return mNovelList;
    }

    public void setNovelList(List<NovelModel> mNovelList) {
        this.mNovelList = mNovelList;
    }

    public void setCurrentCategory(String currentCategory) {
        this.mCurrentCategory = currentCategory;
    }

    public abstract int getLayoutId();


}
