package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.SearchModule;
import org.cryse.novelreader.application.qualifier.FragmentScope;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.ui.SearchFragment;

import dagger.Subcomponent;

@FragmentScope
@Subcomponent(
        modules = SearchModule.class
)
public interface SearchFragmentComponent {
    SearchFragment inject(SearchFragment fragment);

    NovelListPresenter presenter();
}