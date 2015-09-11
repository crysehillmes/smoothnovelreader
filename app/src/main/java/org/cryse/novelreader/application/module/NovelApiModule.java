package org.cryse.novelreader.application.module;


import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import org.cryse.novelreader.application.qualifier.ApplicationContext;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.data.NovelDatabaseAccessLayerImpl;
import org.cryse.novelreader.lib.novelsource.baidubrowser.BaiduBrowserNovelSource;
import org.cryse.novelreader.lib.novelsource.easou.EasouNovelSource;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.logic.impl.NovelBusinessLogicLayerImpl;
import org.cryse.novelreader.logic.impl.NovelSourceManager;
import org.cryse.novelreader.util.NovelTextFilter;
import org.cryse.novelreader.util.parser.NovelTextSimplifyFilter;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NovelApiModule {
    private static final int NETWORK_TIMEOUT = 30;
    @Provides
    NovelTextFilter provideNovelTextFilter() {
        return new NovelTextSimplifyFilter();
    }

    @Provides
    @Singleton
    NovelSourceManager provideNovelSourceManager() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        NovelSourceManager manager = new NovelSourceManager();
        manager.registerNovelSource(
                EasouNovelSource.SOURCE_EASOU,
                EasouNovelSource.build(okHttpClient)
        );
        manager.registerNovelSource(
                BaiduBrowserNovelSource.SOURCE_BAIDU_BROWSER,
                BaiduBrowserNovelSource.build(okHttpClient)
        );
        return manager;
    }

    @Provides
    @Singleton
    NovelDatabaseAccessLayer provideNovelDatabaseService(@ApplicationContext Context context) {
        return new NovelDatabaseAccessLayerImpl(context);
    }

    @Provides
    @Singleton
    NovelBusinessLogicLayer provideNovelDataService(NovelSourceManager novelSourceManager, NovelDatabaseAccessLayer dataBaseService, NovelTextFilter novelTextFilter) {
        return new NovelBusinessLogicLayerImpl(novelSourceManager, dataBaseService, novelTextFilter);
    }
}
