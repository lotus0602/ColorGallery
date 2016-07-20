package com.example.n.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.n.colorgallery.R;
import com.example.n.model.ColorSwatch;

import java.util.List;

/**
 * Created by N on 2016-07-17.
 */
public class ColorFavoriteAdapter extends RecyclerView.Adapter<ColorFavoriteAdapter.ViewHolder>{
    private List<ColorSwatch> mFavorite;

    public ColorFavoriteAdapter(List<ColorSwatch> favorite) {
        mFavorite = favorite;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.item_color_favorite_iv);
            textView = (TextView) itemView.findViewById(R.id.item_color_favorite_tv);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View inflateView = inflater.inflate(R.layout.item_rv_color_favorite, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflateView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorSwatch swatch = mFavorite.get(position);
        ImageView imageView = holder.imageView;
        TextView textView = holder.textView;

        imageView.setBackgroundColor(swatch.getRgb());
        String s = "RGB : #" + Integer.toHexString(swatch.getRgb()).toUpperCase();
        textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return mFavorite.size();
    }
}
