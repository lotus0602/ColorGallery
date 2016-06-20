package com.example.n.model;

import android.net.Uri;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by N on 2016-06-17.
 */
public class ColorGallery extends RealmObject {
    private Date createdAt;
    private String imageString;

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getImageString() {
        return imageString;
    }

    public Uri getImageUri() {
        return Uri.parse(this.imageString);
    }
}
