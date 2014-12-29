package org.cryse.novelreader.modules.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.cryse.novelreader.Constants;
import org.cryse.novelreader.data.DaoMaster;
import org.cryse.novelreader.data.DaoSession;
import org.cryse.novelreader.data.NovelBookMarkModelDao;
import org.cryse.novelreader.data.NovelChapterContentModelDao;
import org.cryse.novelreader.data.NovelChapterModelDao;
import org.cryse.novelreader.data.NovelModelDao;
import org.cryse.novelreader.logic.NovelDataService;
import org.cryse.novelreader.logic.impl.NovelDataServiceImpl;
import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.source.baidu.BaiduNovelSourceImpl;
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
        return new BaiduNovelSourceImpl();
    }

    @Provides
    @Singleton
    DaoMaster provideDaoMaster(@ApplicationContext Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constants.DATABASE_NAME, null);
        SQLiteDatabase sqliteDatabase = helper.getWritableDatabase();
        return new DaoMaster(sqliteDatabase);
    }

    @Provides
    DaoSession provideDaoSession(DaoMaster daoMaster) {
        return daoMaster.newSession();
    }

    @Provides
    NovelModelDao provideNovelModelDao(DaoSession daoSession) {
        return daoSession.getNovelModelDao();
    }

    @Provides
    NovelChapterModelDao provideNovelChapterModelDao(DaoSession daoSession) {
        return daoSession.getNovelChapterModelDao();
    }

    @Provides
    NovelChapterContentModelDao provideNovelChapterContentModelDao(DaoSession daoSession) {
        return daoSession.getNovelChapterContentModelDao();
    }

    @Provides
    NovelBookMarkModelDao provideNovelBookMarkModelDao(DaoSession daoSession) {
        return daoSession.getNovelBookMarkModelDao();
    }

    @Provides
    NovelDataService provideNovelDataService(NovelSource novelSource, NovelModelDao novelModelDao, NovelChapterModelDao novelChapterModelDao, NovelChapterContentModelDao novelChapterContentModelDao, NovelBookMarkModelDao novelBookMarkModelDao, NovelTextFilter novelTextFilter) {
        return new NovelDataServiceImpl(novelSource, novelModelDao, novelChapterModelDao, novelChapterContentModelDao, novelBookMarkModelDao, novelTextFilter);
    }
}
