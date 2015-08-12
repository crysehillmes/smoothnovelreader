package org.cryse.novelreader.modules.provider;

import android.content.Context;

import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.data.NovelDatabaseAccessLayerImpl;
import org.cryse.novelreader.lib.novelsource.baidubrowser.BaiduBrowserNovelSourceImpl;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.logic.impl.NovelBusinessLogicLayerImpl;
import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.NovelTextFilter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
            ContextProvider.class,
            UtilProvider.class
        }
)
public class NovelSourceProvider {

    @Provides
    @Singleton
    NovelSource provideNovelSource() {
        return new BaiduBrowserNovelSourceImpl();
    }


    @Provides
    NovelDatabaseAccessLayer provideNovelDataBaseService(@ApplicationContext Context context) {
        return new NovelDatabaseAccessLayerImpl(context);
    }

    @Provides
    NovelBusinessLogicLayer provideNovelDataService(NovelSource novelSource, NovelDatabaseAccessLayer dataBaseService, NovelTextFilter novelTextFilter) {
        return new NovelBusinessLogicLayerImpl(novelSource, dataBaseService, novelTextFilter);
    }
}
