package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.AppModule;
import org.cryse.novelreader.application.module.BookShelfFragmentModule;
import org.cryse.novelreader.application.module.ChapterCacheModule;
import org.cryse.novelreader.application.module.ChaptersActivityModule;
import org.cryse.novelreader.application.module.DetailActivityModule;
import org.cryse.novelreader.application.module.LocalFileImportModule;
import org.cryse.novelreader.application.module.NovelApiModule;
import org.cryse.novelreader.application.module.ReadActivityModule;
import org.cryse.novelreader.application.module.ReadOptionsModule;
import org.cryse.novelreader.application.module.SearchModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                NovelApiModule.class
        }
)
public interface AppComponent {
    BookShelfFragmentComponent plus(BookShelfFragmentModule bookShelfFragmentModule);

    ChaptersActivityComponent plus(ChaptersActivityModule chaptersActivityModule);

    SearchFragmentComponent plus(SearchModule searchFragmentModule);

    ReadActivityComponent plus(ReadActivityModule readActivityModule);

    DetailActivityComponent plus(DetailActivityModule detailActivityModule);

    ReadOptionsComponent plus(ReadOptionsModule readOptionsModule);

    ChapterCacheComponent plus(ChapterCacheModule chapterCacheModule);

    TextImportComponent plus(LocalFileImportModule textImportModule);
}