package com.rachitsharma300.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface UserDao {

    @Insert
    void addUser(User user);

    @Query("SELECT * FROM user WHERE username = :username")
    User getUser(String username);

    @Query("SELECT COUNT(*) FROM user")
    int getNumOfUsers();

    @Query("SELECT img_salt FROM user WHERE username = :username")
    String getImgSalt(String username);
}
