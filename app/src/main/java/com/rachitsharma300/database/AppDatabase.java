package com.rachitsharma300.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import javax.inject.Singleton;

@Singleton
@Database(entities = {Image.class, User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Get ImageDao
     *
     * @return ImageDao
     */
    public abstract ImageDao imgDao();

    /**
     * Get UserDao
     *
     * @return UserDao
     */
    public abstract UserDao userDao();
}
