package com.example.n.model;

import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by N on 2016-06-20.
 */
public class ColorSwatch extends RealmObject {
    private String originalUri;
    private boolean isFavorite;

    private int rgb;
    private int bodyTextColor;
    private int titleTextColor;
    private int population;
//    private float[] hsl;
    private float hue;
    private float saturation;
    private float lightness;

    public ColorSwatch() {
    }

    public ColorSwatch(String uri, Palette.Swatch swatch) {
        this.originalUri = uri;
        this.isFavorite = false;

        this.rgb = swatch.getRgb();
        this.bodyTextColor = swatch.getBodyTextColor();
        this.titleTextColor = swatch.getTitleTextColor();
        this.population = swatch.getPopulation();
//        this.hsl = swatch.getHsl();
        this.hue = swatch.getHsl()[0];
        this.saturation = swatch.getHsl()[1];
        this.lightness = swatch.getHsl()[2];
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getRgb() {
        return rgb;
    }

    public int getBodyTextColor() {
        return bodyTextColor;
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public int getPopulation() {
        return population;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getLightness() {
        return lightness;
    }
//    public float[] getHsl() {
//        return hsl;
//    }

    public static ArrayList<ColorSwatch> createList(String uri, List<Palette.Swatch> swatches) {
        ArrayList<ColorSwatch> list = new ArrayList<>();

        for (Palette.Swatch s : swatches) {
            list.add(new ColorSwatch(uri, s));
        }
        return list;
    }
}
