package com.rachitsharma300.services;

import android.content.Context;

import androidx.room.Room;

import com.rachitsharma300.activities.AppLifeCycleManager;
import com.rachitsharma300.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

    private static final String DB_NAME = "imageEncrypter.db";
    private Context appContext;

    public AppModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    public AppDatabase providesAppDatabase() {
        return Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME).build();
    }

    @Singleton
    @Provides
    AuthService provideAuthService() {
        return new AuthService();
    }

    @Provides
    @Singleton
    ExecService providesThreadManager() {
        return new ExecService();
    }

    @Provides
    @Singleton
    AppLifeCycleManager providesAppLifeCycleManager() {
        return new AppLifeCycleManager(appContext);
    }
}
