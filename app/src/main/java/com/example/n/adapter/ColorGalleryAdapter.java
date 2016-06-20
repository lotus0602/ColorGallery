package com.example.n.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.n.colorgallery.R;
import com.example.n.model.ColorGallery;
import com.squareup.picasso.Picasso;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by N on 2016-06-17.
 */
public class ColorGalleryAdapter extends RecyclerView.Adapter<ColorGalleryAdapter.ViewHolder>{
    private Context context;
    private RealmResults<ColorGallery> realmResults;
    private static OnItemClickListener listener;

    public ColorGalleryAdapter(RealmResults<ColorGallery> results) {
        realmResults = results;
        realmResults.addChangeListener(new RealmChangeListener<RealmResults<ColorGallery>>() {
            @Override
            public void onChange(RealmResults<ColorGallery> element) {
                notifyDataSetChanged();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.item_color_gallery_iv);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_rv_color_gallery, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorGallery colorGallery = realmResults.get(position);
        ImageView imageView = holder.imageView;

        Picasso.with(context).load(colorGallery.getImageUri())
                .fit().centerCrop().into(imageView);
    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        ColorGalleryAdapter.listener = listener;
    }
}
