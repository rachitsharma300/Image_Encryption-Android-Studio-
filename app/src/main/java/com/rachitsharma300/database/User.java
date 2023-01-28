package com.rachitsharma300.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey
    @ColumnInfo(name = "username")
    @NonNull
    private String username;

    @ColumnInfo(name = "hash")
    @NonNull
    private byte[] hash;

    @ColumnInfo(name = "pw_salt")
    @NonNull
    private String pw_salt;

    @ColumnInfo(name = "img_salt")
    @NonNull
    private String img_salt;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public byte[] getHash() {
        return hash;
    }

    public void setHash(@NonNull byte[] hash) {
        this.hash = hash;
    }

    @NonNull
    public String getPw_salt() {
        return pw_salt;
    }

    public void setPw_salt(@NonNull String pw_salt) {
        this.pw_salt = pw_salt;
    }

    @NonNull
    public String getImg_salt() {
        return img_salt;
    }

    public void setImg_salt(@NonNull String img_salt) {
        this.img_salt = img_salt;
    }
}
