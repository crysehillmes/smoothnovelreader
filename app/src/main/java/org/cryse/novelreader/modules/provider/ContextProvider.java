package org.cryse.novelreader.modules.provider;

import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.res.AssetManager;

import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class ContextProvider {

    private final Context mApplicationContext;

    public ContextProvider(Context context) {
        mApplicationContext = context;
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
    AndroidDisplay provideAndroidDisplay() {
        return ((SmoothReaderApplication)mApplicationContext).getAndroidDisplay();
    }

    @Provides @Singleton
    public AccountManager provideAccountManager() {
        return AccountManager.get(mApplicationContext);
    }

    @Provides
    public File providePrivateFileDirectory() {
        return mApplicationContext.getFilesDir();
    }

    @Provides @Singleton
    public AssetManager provideAssetManager() {
        return mApplicationContext.getAssets();
    }

    @Provides @Singleton
    public AlarmManager provideAlarmManager() {
        return (AlarmManager) mApplicationContext.getSystemService(Context.ALARM_SERVICE);
    }

}