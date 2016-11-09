package com.example.n.fragment;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.n.colorgallery.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorCameraPreviewFragment extends Fragment
        implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private RelativeLayout root;
    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;
    private ImageView colorView;
    private TextView colorCodeView;

    private Camera camera = null;
    private boolean previewing = false;

    public ColorCameraPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_color_camera_preview, container, false);
        root = (RelativeLayout) v.findViewById(R.id.camera_preview_root);
        cameraPreview = (SurfaceView) v.findViewById(R.id.camera_preview_surfaceview);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceHolder = cameraPreview.getHolder();
        surfaceHolder.addCallback(this);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View overlayView = inflater.inflate(R.layout.overlay_color_info_box, root, false);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        overlayView.setLayoutParams(params);

        if (getView() != null) {
            ((RelativeLayout) getView()).addView(overlayView);
        }

        colorView = (ImageView) overlayView.findViewById(R.id.overlay_color_info_iv);
        colorCodeView = (TextView) overlayView.findViewById(R.id.overlay_color_info_tv);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        if (camera != null) {
            try {
                camera.setPreviewCallback(this);
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;

        final int[] rgb = decodeYUV420SP(data, width, height);

        Bitmap bitmap = Bitmap.createBitmap(rgb, width, height, Bitmap.Config.ARGB_8888);

        int x = width / 2;
        int y = height / 2;
        int targetColor = bitmap.getPixel(x, y);
        colorView.setBackgroundColor(targetColor);
        colorCodeView.setText("#" + Integer.toHexString(targetColor));

        Log.d("Size", "Width : " + width + ", Height : " + height);
        Log.d("Center Position", "X : " + x + ", Y : " + y);
        Log.d("Pixel", "" + targetColor);
        Log.d("Color Code", "#" + Integer.toHexString(targetColor));
    }

    private int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        int rgb[] = new int[width * height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }
}
