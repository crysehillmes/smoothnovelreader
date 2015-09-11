package org.cryse.novelreader.application.module;

import org.cryse.novelreader.application.qualifier.ServiceScope;
import org.cryse.novelreader.service.LocalFileImportService;

import dagger.Module;
import dagger.Provides;

@Module
public class LocalFileImportModule {
    private LocalFileImportService localFileImportService;

    public LocalFileImportModule(LocalFileImportService localFileImportService) {
        this.localFileImportService = localFileImportService;
    }

    @Provides
    @ServiceScope
    LocalFileImportService provideReadOptionsFragment() {
        return localFileImportService;
    }
}