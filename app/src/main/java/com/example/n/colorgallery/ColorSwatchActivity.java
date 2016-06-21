package com.example.n.colorgallery;

import android.content.Intent;
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
import android.widget.ImageView;

import com.example.n.adapter.ColorSwatchAdapter;
import com.example.n.model.ColorGallery;
import com.example.n.model.ColorSwatch;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class ColorSwatchActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ColorSwatchAdapter adapter;
    private ImageView imageView;
    private FloatingActionButton fab;

    private ArrayList<ColorSwatch> colorSwatches;
    private String tag = "";

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_swatch);

        Intent intent = getIntent();
        final Uri uri = intent.getData();
        tag = intent.getStringExtra(MainActivity.TAG_INTENT_KEY);
        Bitmap bitmap = null;

        init();

        collapsingToolbarLayout.setTitle("ColorSwatch");
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Picasso.with(this).load(uri).fit().centerCrop().into(imageView);

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
                    colorSwatches.addAll(ColorSwatch.createList(list));
                    adapter.notifyDataSetChanged();

                    int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                    int primary = getResources().getColor(R.color.colorPrimary);
                    collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                    collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                }
            });
        }

        if (tag.equals(MainActivity.TAG_INTENT_NEW)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ColorGallery colorGallery = realm.createObject(ColorGallery.class);
                            colorGallery.setCreatedAt(new Date());
                            colorGallery.setImageString(uri.toString());
                        }
                    });
                    finish();
                }
            });
        }
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
        fab = (FloatingActionButton) findViewById(R.id.swatch_fab);
        colorSwatches = new ArrayList<>();

        adapter = new ColorSwatchAdapter(colorSwatches);

        realm = Realm.getDefaultInstance();
    }
}
