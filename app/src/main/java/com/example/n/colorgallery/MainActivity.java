package com.example.n.colorgallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.n.adapter.ColorGalleryAdapter;
import com.example.n.animation.SideMenuAnimator;
import com.example.n.listener.OnItemClickListener;
import com.example.n.model.ColorGallery;
import com.example.n.model.SideMenuItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity
        implements SideMenuAnimator.SideMenuAnimatorListener{
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_SELECT_PHOTO = 2;
    public static final String TAG_INTENT_KEY = "tag";
    public static final String TAG_INTENT_NEW = "new";
    public static final String TAG_INTENT_DETAIL = "detail";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout sideMenu;
    private List<SideMenuItem> menuItemList;
    private SideMenuAnimator sideMenuAnimator;

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ColorGalleryAdapter adapter;

    private Realm realm;
    private RealmResults<ColorGallery> realmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        realmResults = realm.where(ColorGallery.class)
                .findAllSortedAsync("createdAt", Sort.DESCENDING);

        adapter = new ColorGalleryAdapter(realmResults);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.item_color_gallery_iv:
                        Intent intent = new Intent(getApplicationContext(), ColorSwatchActivity.class);
                        intent.setData(realmResults.get(position).getImageUri());
                        intent.putExtra(TAG_INTENT_KEY, TAG_INTENT_DETAIL);
                        startActivity(intent);
                        break;
                    case R.id.item_color_gallery_preview:
                        Log.i("Item Click", "Click Preview");
                        break;
                    case R.id.item_color_gallery_delete:
                        Log.i("Item Click", "Click Delete");
                        alertToDelete(position);
                        break;
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        setActionBar();
        createSideMenuList();
        sideMenuAnimator = new SideMenuAnimator(
                this, drawerLayout, sideMenu, menuItemList, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA | requestCode == REQUEST_SELECT_PHOTO) {
                ColorGallery colorGallery = realm.where(ColorGallery.class)
                        .equalTo("imageString", data.getData().toString()).findFirst();

                if (colorGallery == null) {
                    Intent intent = new Intent(this, ColorSwatchActivity.class);
                    intent.setData(data.getData());
                    intent.putExtra(TAG_INTENT_KEY, TAG_INTENT_NEW);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Selected Image Uri Exist!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void init(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sideMenu = (LinearLayout) findViewById(R.id.side_menu);
        recyclerView = (RecyclerView) findViewById(R.id.main_rv_color_gallery);
        fab = (FloatingActionButton) findViewById(R.id.main_fab);

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
        realm = Realm.getDefaultInstance();
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                sideMenu.removeAllViews();
                sideMenu.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && sideMenu.getChildCount() == 0) {
                    sideMenuAnimator.showSideMenu();
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void createSideMenuList() {
        menuItemList = new ArrayList<>();
        menuItemList.add(new SideMenuItem(R.drawable.ic_collections_black_48dp));
        menuItemList.add(new SideMenuItem(R.drawable.ic_camera_black_48dp));
        menuItemList.add(new SideMenuItem(R.drawable.ic_palette_black_48dp));
        menuItemList.add(new SideMenuItem(R.drawable.ic_star_black_48dp));
    }

    private void selectImage() {
        final CharSequence[] selectItems = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(selectItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                if (selectItems[which].equals("Take Photo")) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (selectItems[which].equals("Choose from Gallery")) {
                    intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select"), REQUEST_SELECT_PHOTO);
                }
            }
        });
        builder.show();
    }

    private void alertToDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_message_delete));

        String positiveText = getString(android.R.string.ok);

        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ColorGallery item = realmResults.get(position);
                        item.deleteFromRealm();
                    }
                });
            }
        });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void enableHomeButton(boolean enable) {
        getSupportActionBar().setHomeButtonEnabled(enable);
    }

    @Override
    public void addViewToContainer(View view) {
        sideMenu.addView(view);
    }
}
