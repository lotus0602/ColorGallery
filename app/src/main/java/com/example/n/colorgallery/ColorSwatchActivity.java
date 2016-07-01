package com.example.n.colorgallery;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.n.adapter.ColorSwatchAdapter;
import com.example.n.listener.OnItemClickListener;
import com.example.n.model.ColorGallery;
import com.example.n.model.ColorSwatch;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class ColorSwatchActivity extends AppCompatActivity {
    private static final String TAG_STRING_HUE = "hue";
    private static final String TAG_STRING_SATURATION = "saturation";
    private static final String TAG_STRING_LIGHTNESS = "lightness";
    private static final String TAG_STRING_POPULATION = "population";

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ColorSwatchAdapter adapter;
    private ImageView imageView;
    private Button btnHue, btnSaturation, btnLightness, btnPopulation;
    private FloatingActionButton fab;

    private ArrayList<ColorSwatch> colorSwatches;
    private ColorGallery colorGallery;
    private String tag = "";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_swatch);

        Intent intent = getIntent();
        final Uri uri = intent.getData();
        tag = intent.getStringExtra(MainActivity.TAG_INTENT_KEY);

        init();

        Picasso.with(this).load(uri).fit().centerCrop().into(imageView);

        if (tag.equals(MainActivity.TAG_INTENT_NEW)) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        List<Palette.Swatch> list = palette.getSwatches();
                        colorSwatches.addAll(ColorSwatch.createList(uri.toString(), list));
                        adapter.notifyDataSetChanged();

                        colorGallery = new ColorGallery();
                        colorGallery.setColorOfPalette(getBaseContext(), palette);

                        collapsingToolbarLayout.setContentScrimColor(colorGallery.getMutedColor());
                        collapsingToolbarLayout.setStatusBarScrimColor(colorGallery.getDarkMutedColor());
                        fab.setBackgroundTintList(ColorStateList.valueOf(colorGallery.getVibrantColor()));
                    }
                });
            }

            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ColorGallery gallery = realm.copyToRealm(colorGallery);
                            gallery.setCreatedAt(new Date());
                            gallery.setImageString(uri.toString());
                            gallery.setSwatches(realm.copyToRealm(colorSwatches));
                        }
                    });
                    finish();
                }
            });

        } else if (tag.equals(MainActivity.TAG_INTENT_DETAIL)) {
            ColorGallery gallery = realm.where(ColorGallery.class)
                    .equalTo("imageString", uri.toString()).findFirst();
            colorSwatches.addAll(gallery.getSwatches());
            adapter.notifyDataSetChanged();

            collapsingToolbarLayout.setContentScrimColor(gallery.getMutedColor());
            collapsingToolbarLayout.setStatusBarScrimColor(gallery.getDarkMutedColor());
        }

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void init() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.swatch_rv_color_swatch);
        imageView = (ImageView) findViewById(R.id.swatch_iv_picture);
        btnHue = (Button) findViewById(R.id.alignment_btn_hue);
        btnSaturation = (Button) findViewById(R.id.alignment_btn_saturation);
        btnLightness = (Button) findViewById(R.id.alignment_btn_lightness);
        btnPopulation = (Button) findViewById(R.id.alignment_btn_population);
        fab = (FloatingActionButton) findViewById(R.id.swatch_fab);
        colorSwatches = new ArrayList<>();

        adapter = new ColorSwatchAdapter(colorSwatches);
        btnHue.setOnClickListener(btnClickListener);
        btnSaturation.setOnClickListener(btnClickListener);
        btnLightness.setOnClickListener(btnClickListener);
        btnPopulation.setOnClickListener(btnClickListener);

        collapsingToolbarLayout.setTitle("ColorSwatch");
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent));

        realm = Realm.getDefaultInstance();
    }

    private void sortList(final String s) {
        Collections.sort(colorSwatches, new Comparator<ColorSwatch>() {
            @Override
            public int compare(ColorSwatch lhs, ColorSwatch rhs) {
                switch (s) {
                    case TAG_STRING_HUE:
                        return lhs.getHue() > rhs.getHue() ? -1 :
                                lhs.getHue() < rhs.getHue() ? 1 : 0;
                    case TAG_STRING_SATURATION:
                        return lhs.getSaturation() > rhs.getSaturation() ? -1 :
                                lhs.getSaturation() < rhs.getSaturation() ? 1 : 0;
                    case TAG_STRING_LIGHTNESS:
                        return lhs.getLightness() > rhs.getLightness() ? -1 :
                                lhs.getLightness() < rhs.getLightness() ? 1 : 0;
                    case TAG_STRING_POPULATION:
                        return lhs.getPopulation() > rhs.getPopulation() ? -1 :
                                lhs.getPopulation() < rhs.getPopulation() ? 1 : 0;
                }
                return 0;
            }
        });
    }

    Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.alignment_btn_hue:
                    sortList(TAG_STRING_HUE);
                    break;
                case R.id.alignment_btn_saturation:
                    sortList(TAG_STRING_SATURATION);
                    break;
                case R.id.alignment_btn_lightness:
                    sortList(TAG_STRING_LIGHTNESS);
                    break;
                case R.id.alignment_btn_population:
                    sortList(TAG_STRING_POPULATION);
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };
}
