package com.example.n.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.n.adapter.ColorGalleryAdapter;
import com.example.n.colorgallery.ColorSwatchActivity;
import com.example.n.colorgallery.MainActivity;
import com.example.n.colorgallery.R;
import com.example.n.listener.OnItemClickListener;
import com.example.n.model.ColorGallery;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorGalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ColorGalleryAdapter adapter;
    private Realm realm;
    private RealmResults<ColorGallery> realmResults;

    public ColorGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color_gallery, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_color_gallery_rv);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();
        realmResults = realm.where(ColorGallery.class)
                .findAllSortedAsync("createdAt", Sort.DESCENDING);
        adapter = new ColorGalleryAdapter(realmResults);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.item_color_gallery_iv:
                        Intent intent = new Intent(getContext(), ColorSwatchActivity.class);
                        intent.setData(realmResults.get(position).getImageUri());
                        intent.putExtra(MainActivity.TAG_INTENT_KEY, MainActivity.TAG_INTENT_DETAIL);
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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void alertToDelete(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.dialog_message_delete);

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
}
