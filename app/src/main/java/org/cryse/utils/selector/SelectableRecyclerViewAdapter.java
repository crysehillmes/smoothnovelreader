package org.cryse.utils.selector;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SelectableRecyclerViewAdapter<
        ItemType,
        ItemCollectionType extends List<ItemType>,
        VH extends RecyclerView.ViewHolder
        > extends RecyclerView.Adapter<VH> {
    private ItemCollectionType mItems;
    private SparseBooleanArray mSelectedIndices = new SparseBooleanArray();
    private OnSelectionListener mOnSelectionListener;

    public SelectableRecyclerViewAdapter() {
        mItems = buildItemsCollection();
    }

    public abstract ItemCollectionType buildItemsCollection();

    public ItemCollectionType getItems() {
        return mItems;
    }

    public ItemType getItem(int position) {
        return getItems().get(position);
    }

    public void replaceWith(Collection<ItemType> items) {
        replaceWith(items, false);
    }

    public void replaceWith(Collection<ItemType> items, boolean cleanToReplace) {
        if (items == null) return;
        clearSelection(false);
        if (cleanToReplace) {
            clear();
            addAll(items);
        } else {
            int count = Math.max(mItems.size(), items.size());
            mItems.clear();
            mItems.addAll(items);
            notifyItemRangeRemoved(0, count);
            //notifyItemRangeInserted(0, newCount);
        }
    }

    public void addItem(ItemType remoteFile) {
        if (remoteFile == null) return;
        if (!mItems.contains(remoteFile)) {
            mItems.add(remoteFile);
            notifyItemInserted(mItems.size() - 1);
        }/* else {
            mFiles.set(mItems..indexOf(remoteFile), remoteFile);
            notifyItemChanged(mFiles.indexOf(remoteFile));
        }*/
    }

    public void addAll(Collection<ItemType> files) {
        if (files == null) return;
        int currentCount = mItems.size();
        int newFilesCount = files.size();
        mItems.addAll(files);
        notifyItemRangeInserted(currentCount, newFilesCount);
    }

    public void remove(int position) {
        if (isSelected(position)) {
            setSelection(position, false);
        }
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = mItems.size();
        mItems.clear();
        notifyItemRangeRemoved(0, size);
        clearSelection(false);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setSelection(int position, boolean selection) {
        int currentSelectionCount = mSelectedIndices.size();
        if (selection) {
            mSelectedIndices.put(position, true); // select
        } else {
            mSelectedIndices.delete(position); // de-select
        }
        int newSelectionCount = mSelectedIndices.size();
        notifyItemChanged(position);
        if (mOnSelectionListener != null) {
            if (currentSelectionCount == 0 && newSelectionCount > 0) {
                // Here onSelection() must called after onSelectionStart()
                mOnSelectionListener.onSelectionStart();
                mOnSelectionListener.onSelect(newSelectionCount, position);
            } else if (mSelectedIndices.size() == 0) {
                // Here onSelection() must called before onSelectionStart()
                mOnSelectionListener.onDeselect(newSelectionCount, position);
                mOnSelectionListener.onSelectionEnd();
            } else {
                if(selection)
                    mOnSelectionListener.onSelect(newSelectionCount, position);
                else
                    mOnSelectionListener.onDeselect(newSelectionCount, position);
            }
        }
    }

    public void selectAll() {
        int itemCount = mItems.size();
        for(int position = 0; position < itemCount; position++) {
            mSelectedIndices.put(position, true);
        }
        notifyItemRangeChanged(0, itemCount);
        if (mOnSelectionListener !=null) {
            mOnSelectionListener.onSelect(itemCount);
        }
    }

    public void toggleSelection(int position) {
        setSelection(position, !isSelected(position));
    }

    public boolean isSelected(int position) {
        return mSelectedIndices.get(position, false);
    }

    public int getSelectionCount() {
        return mSelectedIndices.size();
    }

    public void clearSelection() {
        clearSelection(true);
    }

    protected void clearSelection(boolean invalidateItems) {
        int lastSize = mSelectedIndices.size();
        int[] positions = new int[lastSize];
        for(int i = 0; i < lastSize; i++) {
            positions[i] = mSelectedIndices.keyAt(i);;
        }
        mSelectedIndices.clear();
        if (invalidateItems) {
            for (int position : positions) {
                notifyItemChanged(position);
            }
        }
        if (mOnSelectionListener != null && lastSize > 0) {
            mOnSelectionListener.onDeselect(0);
            mOnSelectionListener.onSelectionEnd();
        }
    }

    public int[] getSelections() {
        int selectionCount = mSelectedIndices.size();
        int[] selections = new int[selectionCount];
        for (int i = 0; i < selectionCount; i++) {
            selections[i] = mSelectedIndices.keyAt(selectionCount);
        }
        return selections;
    }

    public ItemType[] getSelectionItems(Class<ItemType> itemTypeClass) {
        int selectionCount = mSelectedIndices.size();
        final ItemType[] selectionItems = (ItemType[]) Array.newInstance(itemTypeClass, selectionCount);
        for (int i = 0; i < selectionCount; i++) {
            selectionItems[i] = mItems.get(mSelectedIndices.keyAt(i));
        }
        return selectionItems;
    }

    public boolean isInSelection() {
        return mSelectedIndices.size() > 0;
    }

    public void setOnSelectionListener(OnSelectionListener listener) {
        this.mOnSelectionListener = listener;
    }

    public interface OnSelectionListener {
        void onSelectionStart();
        void onSelectionEnd();
        // When positions is null, means select all or clear selection
        void onSelect(int currentSelectionCount, int...positions);
        void onDeselect(int currentSelectionCount, int...positions);
    }
}
