package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.ChaptersActivityModule;
import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.ui.NovelChapterListActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = ChaptersActivityModule.class
)
public interface ChaptersActivityComponent {
    NovelChapterListActivity inject(NovelChapterListActivity chapterListActivity);

    NovelChaptersPresenter presenter();
}
