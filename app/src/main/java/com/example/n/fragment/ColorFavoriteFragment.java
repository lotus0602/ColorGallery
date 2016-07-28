package com.example.n.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.n.adapter.ColorFavoriteAdapter;
import com.example.n.colorgallery.R;
import com.example.n.model.ColorSwatch;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorFavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private ColorFavoriteAdapter adapter;

    private ProgressDialog dialog;

    private Realm realm;
    private RealmResults<ColorSwatch> realmResults;

    public ColorFavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color_favorite, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_color_favorite_rv);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading...");
        dialog.show();

        realm = Realm.getDefaultInstance();
        realmResults = realm.where(ColorSwatch.class)
                .equalTo("isFavorite", true).findAllAsync();
        realmResults.addChangeListener(loadFavoriteColorListener);

        adapter = new ColorFavoriteAdapter(realmResults);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        realmResults.removeChangeListener(loadFavoriteColorListener);
    }

    private RealmChangeListener<RealmResults<ColorSwatch>> loadFavoriteColorListener = new RealmChangeListener<RealmResults<ColorSwatch>>() {
        @Override
        public void onChange(RealmResults<ColorSwatch> element) {
            adapter.notifyDataSetChanged();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };
}
