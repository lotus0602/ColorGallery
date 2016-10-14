package com.example.n.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.n.adapter.ColorSwatchAdapter;
import com.example.n.colorgallery.MainActivity;
import com.example.n.colorgallery.R;
import com.example.n.listener.OnCollapsingToolbarListener;
import com.example.n.listener.OnItemClickListener;
import com.example.n.model.ColorGallery;
import com.example.n.model.ColorSwatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwatchListFragment extends Fragment {
    private static final String TAG_STRING_HUE = "hue";
    private static final String TAG_STRING_SATURATION = "saturation";
    private static final String TAG_STRING_LIGHTNESS = "lightness";
    private static final String TAG_STRING_POPULATION = "population";

    private RecyclerView recyclerView;
    private ColorSwatchAdapter adapter;
    private Button btnHue, btnSaturation, btnLightness, btnPopulation;
//    private FloatingActionButton fab;

    private OnCollapsingToolbarListener mListener;

    private ArrayList<ColorSwatch> colorSwatches;
    private ColorGallery colorGallery;
    private String tag = "";
    private Uri uri;

    private Realm realm;

    public SwatchListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            uri = Uri.parse(args.getString("uri"));
            tag = args.getString("tag");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCollapsingToolbarListener) {
            mListener = (OnCollapsingToolbarListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCollapsingToolbarListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_swatch_list, container, false);
        init(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (tag.equals(MainActivity.TAG_INTENT_NEW)) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
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

                        colorGallery = new ColorGallery();
                        colorGallery.setColorOfPalette(getContext(), palette);

//                        fab.setBackgroundTintList(ColorStateList.valueOf(colorGallery.getVibrantColor()));

                        if (mListener != null) {
                            mListener.enableCollapsingToolbar(colorGallery.getMutedColor(), colorGallery.getDarkMutedColor());
                            mListener.enableFloatingActionButton(colorGallery.getVibrantColor());
                        }
                    }
                });
            }

//            fab.setVisibility(View.VISIBLE);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    realm.executeTransactionAsync(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            ColorGallery gallery = realm.copyToRealm(colorGallery);
//                            gallery.setCreatedAt(new Date());
//                            gallery.setImageString(uri.toString());
//                            gallery.setSwatches(realm.copyToRealmOrUpdate(colorSwatches));
//                        }
//                    });
//                    getActivity().finish();
//                }
//            });
        } else if (tag.equals(MainActivity.TAG_INTENT_DETAIL)) {
            ColorGallery gallery = realm.where(ColorGallery.class)
                    .equalTo("imageString", uri.toString()).findFirst();
            colorSwatches.addAll(realm.copyFromRealm(gallery.getSwatches()));
            adapter.notifyDataSetChanged();

            if (mListener != null) {
                mListener.enableCollapsingToolbar(gallery.getMutedColor(), gallery.getDarkMutedColor());
            }
        }

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                if (colorSwatches.get(position).isFavorite()) {
                    colorSwatches.get(position).setFavorite(false);
                } else {
                    colorSwatches.get(position).setFavorite(true);
                }

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealmOrUpdate(colorSwatches.get(position));
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        adapter.notifyItemChanged(position);
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void init(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.swatch_rv_color_swatch);
        btnHue = (Button) v.findViewById(R.id.alignment_btn_hue);
        btnSaturation = (Button) v.findViewById(R.id.alignment_btn_saturation);
        btnLightness = (Button) v.findViewById(R.id.alignment_btn_lightness);
        btnPopulation = (Button) v.findViewById(R.id.alignment_btn_population);
//        fab = (FloatingActionButton) v.findViewById(R.id.swatch_fab);

        colorSwatches = new ArrayList<>();
        adapter = new ColorSwatchAdapter(colorSwatches);
        btnHue.setOnClickListener(btnClickListener);
        btnSaturation.setOnClickListener(btnClickListener);
        btnLightness.setOnClickListener(btnClickListener);
        btnPopulation.setOnClickListener(btnClickListener);

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

    public void saveSwatches() {
        realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ColorGallery gallery = realm.copyToRealm(colorGallery);
                            gallery.setCreatedAt(new Date());
                            gallery.setImageString(uri.toString());
                            gallery.setSwatches(realm.copyToRealmOrUpdate(colorSwatches));
                        }
                    });
                    getActivity().finish();
    }

}
