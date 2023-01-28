package com.rachitsharma300.services;

import android.app.Application;

public class App extends Application {
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        App.appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .build();
    }

    /**
     * Get app component
     *
     * @return
     */
    public static AppComponent getAppComponent() {
        return appComponent;
    }
}