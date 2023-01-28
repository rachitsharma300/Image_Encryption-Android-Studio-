package com.rachitsharma300.services;

import android.util.Base64;

import java.util.Arrays;

import com.rachitsharma300.database.AppDatabase;
import com.rachitsharma300.database.User;
import com.rachitsharma300.util.CryptoUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthService {

    @Inject
    protected AppDatabase db;
    /**
     * This key is used to encrypt and decrypt images, this is essentially a hash of
     * (password + img_salt).
     *
     * @see User
     */
    private String imgKey = null;

    public AuthService() {
        App.getAppComponent().inject(this);
    }

    /**
     * Login. If successful, the image key is generated and stored in this service class that will
     * be used for encryption and decryption.
     *
     * @param name name login name
     * @param pw   password login password
     * @return true/false indicating whether the credential is verified.
     */
    public boolean login(String name, String pw) {
        if (name.isEmpty() || pw.isEmpty()) // invalid arguments
            return false;

        User user = db.userDao().getUser(name);
        if (user != null) {
            // compare hashes
            byte[] hash = CryptoUtil.hash(pw, user.getPw_salt());
            if (Arrays.equals(hash, user.getHash())) {
                this.imgKey = Base64.encodeToString(CryptoUtil.hash(pw, db.userDao().getImgSalt(name)), Base64.DEFAULT);
                return true;
            }
        }
        return false;
    }

    /**
     * Persist a new {@code User} (or new credential) in database. Can only register when there is
     * no user in DB.
     *
     * @param name name
     * @param pw   password
     * @return true/false indicating whether the {@code User} is persisted
     */
    public boolean register(String name, String pw) {
        if (isRegistered())
            return false;

        try {
            // create new user
            User user = new User();
            user.setUsername(name);
            user.setPw_salt(CryptoUtil.randSalt());
            user.setImg_salt(CryptoUtil.randSalt());

            // hashing
            byte[] hash = CryptoUtil.hash(pw, user.getPw_salt());
            if (hash == null)
                return false; // Hashing operation failed
            user.setHash(hash);

            // persist user
            db.userDao().addUser(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return whether there is a user registered already
     * @return
     */
    public boolean isRegistered(){
        return db.userDao().getNumOfUsers() > 0;
    }

    public String getImgKey() {
        return imgKey;
    }

    /**
     * Check whether the current user is authenticated
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        return getImgKey() != null;
    }

    /**
     * Sign out the current user. After calling this method, the {@code isAuthenticated()} method
     * will return false;
     */
    public void signOut() {
        imgKey = null;
    }
}
