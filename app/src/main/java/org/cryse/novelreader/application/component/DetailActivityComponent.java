package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.DetailActivityModule;
import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.ui.NovelDetailActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = DetailActivityModule.class
)
public interface DetailActivityComponent {
    NovelDetailActivity inject(NovelDetailActivity activity);

    NovelDetailPresenter presenter();
}