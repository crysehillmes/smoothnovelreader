package org.cryse.novelreader.modules;

import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.modules.provider.ContextProvider;
import org.cryse.novelreader.modules.provider.NovelSourceProvider;
import org.cryse.novelreader.modules.provider.PreferenceProvider;
import org.cryse.novelreader.modules.provider.UtilProvider;

public class ModulesList {

    private ModulesList() {
        // No instances
    }


    public static Object[] list(SmoothReaderApplication application) {
        return new Object[]{
                new AppModule(application),
                new ContextProvider(application, application.getEventBus()),
                new PreferenceProvider(),
                new NovelSourceProvider(),
                new UtilProvider()
        };
    }

}