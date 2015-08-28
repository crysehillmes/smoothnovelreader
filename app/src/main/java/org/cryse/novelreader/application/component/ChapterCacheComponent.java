package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.ChapterCacheModule;
import org.cryse.novelreader.application.qualifier.ServiceScope;
import org.cryse.novelreader.service.ChapterContentsCacheService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(
        modules = ChapterCacheModule.class
)
public interface ChapterCacheComponent {
    ChapterContentsCacheService inject(ChapterContentsCacheService service);
}