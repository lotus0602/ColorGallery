package com.example.n.colorgallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.n.adapter.ColorGalleryAdapter;
import com.example.n.adapter.SpacesItemDecoration;
import com.example.n.model.ColorGallery;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_SELECT_PHOTO = 2;
    public static final String TAG_INTENT_KEY = "tag";
    public static final String TAG_INTENT_NEW = "new";
    public static final String TAG_INTENT_DETAIL = "detail";

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

        realmResults = realm.where(ColorGallery.class).findAllAsync();

        adapter = new ColorGalleryAdapter(realmResults);
        adapter.setOnItemClickListener(new ColorGalleryAdapter.OnItemClickListener() {
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
                        break;
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        recyclerView.addItemDecoration(decoration);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                Intent intent = new Intent(this, ColorSwatchActivity.class);
                intent.setData(data.getData());
                intent.putExtra(TAG_INTENT_KEY, TAG_INTENT_NEW);
                startActivity(intent);
            }
        }
    }

    private void init(){
        recyclerView = (RecyclerView) findViewById(R.id.main_rv_color_gallery);
        fab = (FloatingActionButton) findViewById(R.id.main_fab);

        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(configuration);
        realm = Realm.getDefaultInstance();
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
}
