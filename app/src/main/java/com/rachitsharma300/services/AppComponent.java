package com.rachitsharma300.services;

import com.rachitsharma300.activities.AppLifeCycleManager;
import com.rachitsharma300.activities.ImageListActivity;
import com.rachitsharma300.activities.ImageListAdapter;
import com.rachitsharma300.activities.ImageViewActivity;
import com.rachitsharma300.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity activity);

    void inject(ImageListActivity activity);

    void inject(ImageViewActivity activity);

    void inject(ImageListAdapter adapter);

    void inject(AuthService authService);

    void inject(AppLifeCycleManager lifecycleManager);
}