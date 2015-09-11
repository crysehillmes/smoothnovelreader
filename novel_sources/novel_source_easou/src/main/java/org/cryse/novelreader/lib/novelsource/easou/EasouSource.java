package org.cryse.novelreader.lib.novelsource.easou;

import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.source.NovelSource;

public class EasouSource {
    public static final int TYPE_EASOU_SOURCE = Consts.TYPE_EASOU_SOURCE;
    public static final String TYPE_EASOU_SOURCE_NAME = Consts.TYPE_EASOU_SOURCE_NAME;

    public static NovelSource build(OkHttpClient okHttpClient) {
        return new EasouNovelSourceImpl(okHttpClient);
    }
}
