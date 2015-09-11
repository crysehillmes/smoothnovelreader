package org.cryse.novelreader.application.module;


import android.content.Context;

import org.cryse.novelreader.application.qualifier.ApplicationContext;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.data.NovelDatabaseAccessLayerImpl;
import org.cryse.novelreader.lib.novelsource.easou.EasouNovelSourceImpl;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.logic.impl.NovelBusinessLogicLayerImpl;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.NovelTextFilter;
import org.cryse.novelreader.util.parser.NovelTextSimplifyFilter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NovelApiModule {

    @Provides
    NovelTextFilter provideNovelTextFilter() {
        return new NovelTextSimplifyFilter();
    }

    @Provides
    @Singleton
    NovelSource provideNovelSource() {
        return new EasouNovelSourceImpl();
    }

    @Provides
    @Singleton
    NovelDatabaseAccessLayer provideNovelDatabaseService(@ApplicationContext Context context) {
        return new NovelDatabaseAccessLayerImpl(context);
    }

    @Provides
    @Singleton
    NovelBusinessLogicLayer provideNovelDataService(NovelSource novelSource, NovelDatabaseAccessLayer dataBaseService, NovelTextFilter novelTextFilter) {
        return new NovelBusinessLogicLayerImpl(novelSource, dataBaseService, novelTextFilter);
    }
}
