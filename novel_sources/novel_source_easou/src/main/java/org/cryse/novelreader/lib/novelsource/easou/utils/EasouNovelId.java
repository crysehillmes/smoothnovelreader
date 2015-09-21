package org.cryse.novelreader.lib.novelsource.easou.utils;

public class EasouNovelId {
    public static String toNovelId(long gid, long nid, String title, String author) {
        return Long.toString(gid) + "," + Long.toString(nid) + "," + title + "," + author;
    }

    public static long fromNovelIdToGid(String novelId) {
        String[] tokens = novelId.split(",", 4);
        return Long.valueOf(tokens[0]);
    }

    public static long fromNovelIdToNid(String novelId) {
        String[] tokens = novelId.split(",", 4);
        return Long.valueOf(tokens[1]);
    }
}
