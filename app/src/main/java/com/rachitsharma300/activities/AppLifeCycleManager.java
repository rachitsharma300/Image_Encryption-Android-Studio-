package com.rachitsharma300.activities;

import android.content.Context;
import android.content.Intent;

import com.rachitsharma300.services.App;
import com.rachitsharma300.services.AuthService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppLifeCycleManager {

    @Inject
    protected AuthService auth;
    private Context appContext;

    public AppLifeCycleManager(Context appContext) {
        this.appContext = appContext;
        App.getAppComponent().inject(this);
    }

    /**
     * Restart the app
     * <p>
     * Creates the {@code MainActivity}, clear all remaining activities in the stack, and signout the
     * authenticated user.
     */
    public void restart() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.appContext.startActivity(intent);
        this.auth.signOut();
    }
}
