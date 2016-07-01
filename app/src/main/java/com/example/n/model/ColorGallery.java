package com.example.n.model;

import android.content.Context;
import android.net.Uri;
import android.support.v7.graphics.Palette;

import com.example.n.colorgallery.R;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by N on 2016-06-17.
 */
public class ColorGallery extends RealmObject {
    private Date createdAt;
    private String imageString;
    private RealmList<ColorSwatch> swatches = new RealmList<>();
    private int mutedColor;
    private int vibrantColor;
    private int darkMutedColor;
    private int darkVibrantColor;
    private int lightMutedColor;
    private int lightVibrantColor;

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public void setSwatches(List<ColorSwatch> swatches) {
        this.swatches.addAll(swatches);
    }

    public void setColorOfPalette(Context context, Palette palette) {
        int primary = context.getResources().getColor(R.color.colorPrimary);
        int primaryDark = context.getResources().getColor(R.color.colorPrimaryDark);
        int accent = context.getResources().getColor(R.color.colorAccent);

        this.mutedColor = palette.getMutedColor(primary);
        this.darkMutedColor = palette.getDarkMutedColor(primaryDark);
        this.lightMutedColor = palette.getLightMutedColor(primary);
        this.vibrantColor = palette.getVibrantColor(accent);
        this.darkVibrantColor = palette.getDarkVibrantColor(accent);
        this.lightVibrantColor = palette.getLightVibrantColor(accent);
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

    public RealmList<ColorSwatch> getSwatches() {
        return swatches;
    }

    public int getMutedColor() {
        return mutedColor;
    }

    public int getVibrantColor() {
        return vibrantColor;
    }

    public int getDarkMutedColor() {
        return darkMutedColor;
    }

    public int getDarkVibrantColor() {
        return darkVibrantColor;
    }

    public int getLightMutedColor() {
        return lightMutedColor;
    }

    public int getLightVibrantColor() {
        return lightVibrantColor;
    }
}
