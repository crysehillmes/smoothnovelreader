package org.cryse.novelreader.application.component;

import org.cryse.novelreader.application.module.LocalFileImportModule;
import org.cryse.novelreader.application.qualifier.ServiceScope;
import org.cryse.novelreader.service.LocalFileImportService;

import dagger.Subcomponent;

@ServiceScope
@Subcomponent(
        modules = LocalFileImportModule.class
)
public interface TextImportComponent {
    LocalFileImportService inject(LocalFileImportService service);
}