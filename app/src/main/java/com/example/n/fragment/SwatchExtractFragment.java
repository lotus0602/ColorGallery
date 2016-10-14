package com.example.n.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.n.colorgallery.R;
import com.example.n.listener.OnCollapsingToolbarListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwatchExtractFragment extends Fragment {

    private RelativeLayout targetContainer;
    private ImageView targetImageView, extractColorView;
    private TextView extractColorTextView;

    private OnCollapsingToolbarListener mListener;

    private Uri uri;
    private Bitmap mBitmap = null;

    public SwatchExtractFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            uri = Uri.parse(args.getString("uri"));
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
        View v = inflater.inflate(R.layout.fragment_swatch_extract, container, false);
        targetContainer = (RelativeLayout) v.findViewById(R.id.extract_color_target_container);
        targetImageView = (ImageView) v.findViewById(R.id.extract_color_target_iv);
        extractColorView = (ImageView) v.findViewById(R.id.extract_color_iv);
        extractColorTextView = (TextView) v.findViewById(R.id.extract_color_tv);

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        targetImageView.getLayoutParams().height = displayMetrics.heightPixels / 2;

        mListener.disableCollapsingToolbar();
        Picasso.with(getContext()).load(uri).fit().into(targetImageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("Callback", "Success");
                mBitmap = ((BitmapDrawable) targetImageView.getDrawable()).getBitmap();
            }

            @Override
            public void onError() {
                Log.d("Callback", "Error");
            }
        });

        targetContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[] {eventX, eventY};

                Matrix invertMatrix = new Matrix();
                targetImageView.getImageMatrix().invert(invertMatrix);
                invertMatrix.mapPoints(eventXY);
                int x = (int)eventXY[0];
                int y = (int)eventXY[1];

                if (x < 0) {
                    x = 0;
                } else if (x > mBitmap.getWidth() - 1) {
                    x = mBitmap.getWidth() - 1;
                }
                if (y < 0) {
                    y = 0;
                } else if (y > mBitmap.getHeight() - 1) {
                    y = mBitmap.getHeight() - 1;
                }
                Log.i("After Touched Position", "eventX : " + eventX + " eventY : " + eventY);
                Log.i("After Touched Position", "X : " + x + " Y : " + y);

                int touchedPixel = mBitmap.getPixel(x, y);
                extractColorView.setBackgroundColor(touchedPixel);
                extractColorTextView.setText("#" + Integer.toHexString(touchedPixel));

                return true;
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_color_swatch, menu);
    }
}
