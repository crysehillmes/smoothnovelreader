package org.cryse.novelreader.modules.provider;

import org.cryse.novelreader.util.NovelTextFilter;
import org.cryse.novelreader.util.parser.NovelTextSimplifyFilter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class UtilProvider {
    @Provides
    NovelTextFilter provideNovelTextFilter() {
        return new NovelTextSimplifyFilter();
    }
}
