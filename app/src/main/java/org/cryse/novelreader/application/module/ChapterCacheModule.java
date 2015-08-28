package org.cryse.novelreader.application.module;

import org.cryse.novelreader.application.qualifier.ServiceScope;
import org.cryse.novelreader.service.ChapterContentsCacheService;

import dagger.Module;
import dagger.Provides;

@Module
public class ChapterCacheModule {
    private ChapterContentsCacheService cacheService;

    public ChapterCacheModule(ChapterContentsCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Provides
    @ServiceScope
    ChapterContentsCacheService provideReadOptionsFragment() {
        return cacheService;
    }
}