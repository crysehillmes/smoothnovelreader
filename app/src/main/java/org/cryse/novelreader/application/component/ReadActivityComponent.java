package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.ReadActivityModule;
import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.ui.NovelReadViewActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = ReadActivityModule.class
)
public interface ReadActivityComponent {
    NovelReadViewActivity inject(NovelReadViewActivity chapterListActivity);

    NovelChapterContentPresenter presenter();
}
