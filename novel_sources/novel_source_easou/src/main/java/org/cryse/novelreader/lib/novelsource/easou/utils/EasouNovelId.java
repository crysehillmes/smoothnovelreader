package org.cryse.novelreader.lib.novelsource.easou.utils;

public class EasouNovelId {
    public static String toNovelId(long gid, long nid) {
        return Long.toString(gid) + "," + Long.toString(nid);
    }

    public static long fromNovelIdToGid(String novelId) {
        String[] tokens = novelId.split(",", 2);
        return Long.valueOf(tokens[0]);
    }

    public static long fromNovelIdToNid(String novelId) {
        String[] tokens = novelId.split(",", 2);
        return Long.valueOf(tokens[1]);
    }
}
