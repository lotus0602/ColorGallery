package com.example.n.model;

import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2016-06-20.
 */
public class ColorSwatch {
    private int rgb;
    private int bodyTextColor;
    private int titleTextColor;
    private int population;
    private float[] hsl;

    public ColorSwatch(Palette.Swatch swatch) {
        this.rgb = swatch.getRgb();
        this.bodyTextColor = swatch.getBodyTextColor();
        this.titleTextColor = swatch.getTitleTextColor();
        this.population = swatch.getPopulation();
        this.hsl = swatch.getHsl();
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

    public float[] getHsl() {
        return hsl;
    }

    public static ArrayList<ColorSwatch> createList(List<Palette.Swatch> swatches) {
        ArrayList<ColorSwatch> list = new ArrayList<>();

        for (Palette.Swatch s : swatches) {
            list.add(new ColorSwatch(s));
        }
        return list;
    }
}
