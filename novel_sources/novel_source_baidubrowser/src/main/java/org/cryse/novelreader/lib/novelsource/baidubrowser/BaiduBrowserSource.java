package org.cryse.novelreader.lib.novelsource.baidubrowser;

import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.source.NovelSource;

public class BaiduBrowserSource {

    public static final int TYPE_BAIDU_BROWSER_SOURCE = Consts.TYPE_BAIDU_BROWSER_SOURCE;
    public static final String TYPE_BAIDU_BROWSER_SOURCE_NAME = Consts.TYPE_BAIDU_BROWSER_SOURCE_NAME;

    public static NovelSource build(OkHttpClient okHttpClient) {
        return new BaiduBrowserNovelSourceImpl(okHttpClient);
    }
}
