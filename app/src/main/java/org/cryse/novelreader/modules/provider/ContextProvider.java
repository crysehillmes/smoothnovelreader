package org.cryse.novelreader.modules.provider;

import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.res.AssetManager;

import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class ContextProvider {

    private final Context mApplicationContext;
    private final RxEventBus mEventBus;
    public ContextProvider(Context context, RxEventBus eventBus) {
        mApplicationContext = context;
        mEventBus = eventBus;
    }

    @Provides
    @ApplicationContext
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Provides
    public SmoothReaderApplication provideSmoothReaderApplication() {
        return (SmoothReaderApplication)mApplicationContext;
    }

    @Provides
    RunTimeStore provideRunTimeStore() {
        return ((SmoothReaderApplication)mApplicationContext).getRunTimeStore();
    }

    @Provides
    AndroidNavigation provideAndroidDisplay() {
        return ((SmoothReaderApplication)mApplicationContext).getAndroidDisplay();
    }

    @Singleton
    @Provides
    public AccountManager provideAccountManager() {
        return AccountManager.get(mApplicationContext);
    }

    @Provides
    public File providePrivateFileDirectory() {
        return mApplicationContext.getFilesDir();
    }

    @Singleton
    @Provides
    public AssetManager provideAssetManager() {
        return mApplicationContext.getAssets();
    }

    @Singleton
    @Provides
    public AlarmManager provideAlarmManager() {
        return (AlarmManager) mApplicationContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Singleton
    @Provides
    public RxEventBus provideRxEventBus() {
        return mEventBus;
    }
}