package org.cryse.novelreader.logic.impl;

import android.support.v4.util.ArrayMap;

import org.cryse.novelreader.source.NovelSource;

public class NovelSourceManager {
    private ArrayMap<Integer, NovelSource> mNovelSourceMap = new ArrayMap<>();

    public void registerNovelSource(Integer typeCode, NovelSource novelSource) {
        this.mNovelSourceMap.put(typeCode, novelSource);
    }

    public boolean isNovelSourceRegistered(Integer typeCode) {
        return this.mNovelSourceMap.containsKey(typeCode);
    }

    public boolean isNovelSourceRegistered(NovelSource novelSource) {
        return this.mNovelSourceMap.containsValue(novelSource);
    }

    public NovelSource getNovelSource(Integer typeCode) {
        return this.mNovelSourceMap.get(typeCode);
    }

    public NovelSource getDefault() {
        return this.mNovelSourceMap.valueAt(0);
    }

    public NovelSource getNovelSourceAt(int index) {
        return mNovelSourceMap.valueAt(index);
    }

    public int getDefaultTypeCode() {
        return this.mNovelSourceMap.keyAt(0);
    }

    public int getNovelSourceCount() {
        return mNovelSourceMap.size();
    }
}
