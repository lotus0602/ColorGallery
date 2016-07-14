package com.example.n.animation;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.n.colorgallery.R;
import com.example.n.model.SideMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by N on 2016-07-08.
 */
public class SideMenuAnimator {
    private final int ANIMATION_DURATION = 300;

    private Activity activity;
    private DrawerLayout drawerLayout;
    private LinearLayout containerView;

    private List<View> viewList;
    private List<SideMenuItem> items;
    private SideMenuAnimatorListener animatorListener;

    public SideMenuAnimator(Activity a, DrawerLayout drawerLayout, LinearLayout linearLayout,
                            List<SideMenuItem> items, SideMenuAnimatorListener animatorListener) {
        this.activity = a;
        this.drawerLayout = drawerLayout;
        this.containerView = linearLayout;
        this.items = items;
        this.animatorListener = animatorListener;

        viewList = new ArrayList<>();
    }

    public void showSideMenu() {
        setViewsClickable(false);
        viewList.clear();
        double size = items.size();

        for (int i = 0; i < size; i++) {
            View viewMenuItem = activity.getLayoutInflater()
                    .inflate(R.layout.item_side_menu, containerView, false);
            final int position = i;
            double delay = 3 * ANIMATION_DURATION * (position / size);

            viewMenuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchItem();
                }
            });

            ((ImageView) viewMenuItem.findViewById(R.id.item_side_menu_iv))
                    .setImageResource(items.get(i).getItemRes());
            viewMenuItem.setVisibility(View.GONE);
            viewMenuItem.setEnabled(false);
            viewList.add(viewMenuItem);
            animatorListener.addViewToContainer(viewMenuItem);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (position < viewList.size()) {
                        animateView(position);
                    }
                    if (position == viewList.size() -1) {
                        setViewsClickable(true);
                    }
                }
            }, (long) delay);
        }
    }

    private void hideSideMenu() {
        setViewsClickable(false);
        double size = items.size();

        for (int i = (int) size; i >= 0; i--) {
            final int position = i;
            double delay = 3 * ANIMATION_DURATION * ((size - position) / size);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (position < viewList.size()) {
                        animateHideView(position);
                    }
                }
            }, (long) delay);
        }
    }

    private void animateView(final int position) {
        final View view = viewList.get(position);
        view.setVisibility(View.VISIBLE);

        Animation animSlideIn = AnimationUtils.loadAnimation(activity, R.anim.slide_in);
        animSlideIn.setDuration(ANIMATION_DURATION);
        animSlideIn.setInterpolator(new OvershootInterpolator());
        animSlideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animSlideIn);
    }

    private void animateHideView(final int position) {
        final View view = viewList.get(position);

        Animation animSlideOut = AnimationUtils.loadAnimation(activity, R.anim.slide_out);
        animSlideOut.setDuration(ANIMATION_DURATION);
        animSlideOut.setInterpolator(new AccelerateDecelerateInterpolator());
        animSlideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
                if (position == 0) {
                    animatorListener.enableHomeButton(true);
                    drawerLayout.closeDrawers();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animSlideOut);
    }

    private void setViewsClickable(boolean clickable) {
        animatorListener.enableHomeButton(false);
        for (View view : viewList) {
            view.setEnabled(clickable);
        }
    }

    private void switchItem() {
        hideSideMenu();
    }

    public interface SideMenuAnimatorListener {
        void addViewToContainer(View view);
        void enableHomeButton(boolean enable);
    }
}
