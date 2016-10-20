package com.example.n.colorgallery;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.n.fragment.SwatchExtractFragment;
import com.example.n.fragment.SwatchListFragment;
import com.example.n.listener.OnCollapsingToolbarListener;
import com.squareup.picasso.Picasso;

public class ColorSwatchActivity extends AppCompatActivity
        implements OnCollapsingToolbarListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private FloatingActionButton fab;

    private String stringUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_swatch);

        Intent intent = getIntent();
        final Uri uri = intent.getData();
        final String tag = intent.getStringExtra(MainActivity.TAG_INTENT_KEY);
        stringUri = uri.toString();

        init();

        Picasso.with(this).load(uri).fit().centerCrop().into(imageView);

        Bundle bundle = new Bundle();
        bundle.putString("uri", stringUri);
        bundle.putString("tag", tag);

        SwatchListFragment fragment = new SwatchListFragment();
        fragment.setArguments(bundle);
        switchFragment(fragment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getSupportFragmentManager()
                        .findFragmentById(R.id.swatch_fragment_container);
                if (currentFragment instanceof SwatchListFragment) {
                    ((SwatchListFragment) currentFragment).saveSwatches();
                }
            }
        });
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView) findViewById(R.id.swatch_iv_picture);
        fab = (FloatingActionButton) findViewById(R.id.swatch_fab);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle("ColorSwatch");
        collapsingToolbarLayout.setExpandedTitleColor(
                getResources().getColor(android.R.color.transparent));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("uri", stringUri);

                SwatchExtractFragment fragment = new SwatchExtractFragment();
                fragment.setArguments(bundle);
                switchFragment(fragment);
            }
        });
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.swatch_fragment_container, fragment).commit();
    }

    @Override
    public void enableCollapsingToolbar(int contentColor, int statusBarColor) {
        imageView.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setContentScrimColor(contentColor);
        collapsingToolbarLayout.setStatusBarScrimColor(statusBarColor);
    }

    @Override
    public void disableCollapsingToolbar() {
        imageView.setVisibility(View.GONE);
        collapsingToolbarLayout.setTitleEnabled(false);
    }

    @Override
    public void enableFloatingActionButton(int color) {
        fab.setVisibility(View.VISIBLE);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
