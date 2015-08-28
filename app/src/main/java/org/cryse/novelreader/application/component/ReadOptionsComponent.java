package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.ReadOptionsModule;
import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.ui.ReadOptionsFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = ReadOptionsModule.class
)
public interface ReadOptionsComponent {
    ReadOptionsFragment inject(ReadOptionsFragment fragment);
}
