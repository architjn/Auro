package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.widget.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by architjn on 06/01/16.
 */
public class UpNextFragment extends Fragment {

    private int colorLight;
    private View mainView;
    private Context context;
    private ImageView backButton;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        context = mainView.getContext();
        mainView.setBackgroundColor(colorLight);
        init();
        changeColorBackToWhite();
        setFunctioning();
        return mainView;
    }

    private void init() {
        backButton = (ImageView) mainView.findViewById(R.id.player_back_button);
    }

    private void setFunctioning() {
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 1 && isVisible())
                    getActivity().getWindow().setStatusBarColor(0x47000000);
                else if (isVisible()) {
                    getBackToLastFragment();
                }
            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBackToLastFragment();
            }
        });
    }

    private void getBackToLastFragment() {
        PlayerFragment fragment = new PlayerFragment();
        fragment.setSlidingUpPanelLayout(slidingUpPanelLayout);
        fragment.setUpNextFragment(this);
        getActivity().getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
                .replace(R.id.panel_holder, fragment)
                .commit();
        fragment.setMiniPlayerAlpha(0);
        slidingUpPanelLayout.setPanelSlideListener(null);
    }

    private void changeColorBackToWhite() {
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorLight,
                ContextCompat.getColor(context, R.color.appBackground));
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mainView.setBackgroundColor((Integer) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void onBackPressed() {
        if (isVisible())
            getBackToLastFragment();
    }

    public void setColorLight(int colorLight) {
        this.colorLight = colorLight;
    }

    public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingUpPanelLayout) {
        this.slidingUpPanelLayout = slidingUpPanelLayout;
    }
}
