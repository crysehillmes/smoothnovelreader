package org.cryse.novelreader.util.comparator;

import android.text.TextUtils;

import org.cryse.novelreader.model.NovelModel;

import java.util.Comparator;

public class NovelSortKeyComparator implements Comparator<NovelModel> {
    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    @Override
    public int compare(NovelModel lhs, NovelModel rhs) {
        int diff = compare(lhs.getSortKey(), rhs.getSortKey());
        if (diff == 0) {
            if (!TextUtils.isEmpty(lhs.getAuthor()) && !TextUtils.isEmpty(rhs.getAuthor())) {
                return lhs.getAuthor().compareTo(rhs.getAuthor());
            } else {
                return diff;
            }

        } else {
            return diff;
        }
    }
}
