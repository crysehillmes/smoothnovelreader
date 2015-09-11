package org.cryse.novelreader.util;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.util.comparator.NovelSortKeyComparator;

import java.util.Collections;
import java.util.List;

public class LDSort {
    public static void sortNovel(List<NovelModel> novels, String queryString) {
        for (NovelModel novel : novels) {
            novel.setSortKey(levenshteinDistance(queryString, novel.getTitle()));
        }
        Collections.sort(novels, new NovelSortKeyComparator());
    }

    public static int levenshteinDistance(String s, String t) {
        // degenerate cSases
        if (s.equals(t)) return 0;
        if (s.length() == 0) return t.length();
        if (t.length() == 0) return s.length();

        // create two work vectors of integer distances
        int[] v0 = new int[t.length() + 1];
        int[] v1 = new int[t.length() + 1];

        // initialize v0 (the previous row of distances)
        // this row is A[0][i]: edit distance for an empty s
        // the distance is just the number of characters to delete from t
        for (int i = 0; i < v0.length; i++)
            v0[i] = i;

        for (int i = 0; i < s.length(); i++) {
            // calculate v1 (current row distances) from the previous row v0

            // first element of v1 is A[i+1][0]
            //   edit distance is delete (i+1) chars from s to match empty t
            v1[0] = i + 1;

            // use formula to fill in the rest of the row
            for (int j = 0; j < t.length(); j++) {
                int cost = (s.charAt(i) == t.charAt(j)) ? 0 : 1;
                v1[j + 1] = Math.min(v1[j] + 1, Math.min(v0[j + 1] + 1, v0[j] + cost));
            }

            // copy v1 (current row) to v0 (previous row) for next iteration
            System.arraycopy(v1, 0, v0, 0, v0.length);
        }

        return v1[t.length()];
    }
}
