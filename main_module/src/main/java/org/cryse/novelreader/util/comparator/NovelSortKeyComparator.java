package org.cryse.novelreader.util.comparator;

import org.cryse.novelreader.model.NovelModel;

import java.util.Comparator;

public class NovelSortKeyComparator implements Comparator<NovelModel> {
    @Override
    public int compare(NovelModel lhs, NovelModel rhs) {
        return lhs.getSortKey().compareTo(rhs.getSortKey());
    }
}
