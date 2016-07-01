package com.example.n.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.n.colorgallery.R;
import com.example.n.listener.OnItemClickListener;
import com.example.n.model.ColorSwatch;

import java.util.List;

/**
 * Created by N on 2016-06-20.
 */
public class ColorSwatchAdapter extends RecyclerView.Adapter<ColorSwatchAdapter.ViewHolder> {
    private List<ColorSwatch> mColorSwatches;
    private static OnItemClickListener listener;

    public ColorSwatchAdapter(List<ColorSwatch> colorSwatches) {
        this.mColorSwatches = colorSwatches;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageButton ibFavorite;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.item_color_swatch_tv);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.item_color_swatch_favorite);

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View inflateView = inflater.inflate(R.layout.item_rv_color_swatch, parent, false);
        ViewHolder viewHolder = new ViewHolder(inflateView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorSwatch colorSwatch = mColorSwatches.get(position);
        TextView textView = holder.textView;

        textView.setBackgroundColor(colorSwatch.getRgb());
        textView.setTextColor(colorSwatch.getTitleTextColor());
        String s = "RGB : #" + Integer.toHexString(colorSwatch.getRgb()).toUpperCase() +
                "\nPopulation : " + colorSwatch.getPopulation();
        textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return mColorSwatches.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        ColorSwatchAdapter.listener = listener;
    }
}
