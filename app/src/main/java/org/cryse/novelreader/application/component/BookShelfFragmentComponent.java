package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.BookShelfFragmentModule;
import org.cryse.novelreader.application.qualifier.FragmentScope;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.ui.NovelBookShelfFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(
        modules = BookShelfFragmentModule.class
)
public interface BookShelfFragmentComponent {
    NovelBookShelfFragment inject(NovelBookShelfFragment fragment);

    NovelBookShelfPresenter presenter();
}
