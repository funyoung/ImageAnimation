package com.funyoung.image.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.Button;
import android.widget.ImageView;
import com.example.android.navigationdrawerexample.R;

import java.util.Locale;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class PlanetAnimationFragment extends PlanetFragment {
    private static final String ARG_PLANET_NUMBER = "planet_number";

    protected PlanetAnimationFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static ImageFragment createInstance(int position) {
        ImageFragment fragment = new PlanetAnimationFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetAnimationFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    protected View getRootView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet_animation, container, false);
        return rootView;
    }


    private ImageView image = null;
    private Button alphabutton = null;
    private Button scalebutton = null;
    private Button rotatebutton = null;
    private Button translatebutton = null;
    protected void onRootViewCreated(View rootView) {
        image = (ImageView) rootView.findViewById(R.id.image);
        alphabutton = (Button) rootView.findViewById(R.id.Alpha);
        scalebutton = (Button) rootView.findViewById(R.id.scale);
        rotatebutton = (Button) rootView.findViewById(R.id.rotate);
        translatebutton = (Button) rootView.findViewById(R.id.translate);
        setOnClickListener();
    }

    private void setOnClickListener() {
        if (null != alphabutton) {
            alphabutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performAlphaAnimation();
                }
            });
        }

        if (null != rotatebutton) {
            rotatebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performRotateAnimation();
                }
            });
        }

        if (null != scalebutton) {
            scalebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performScaleAnimation();
                }
            });
        }

        if (null != translatebutton) {
            translatebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performTranslateAnimation();
                }
            });
        }
    }

    private static final int DURATION = 5000;   // 5s

    // Translating animation, x-axis from 0% to 50%, and
    // y-axis from 0% to 100%.
    private static final float TRANS_FROM_X = 0.0f;
    private static final float TRANS_TO_X = 0.5f;
    private static final float TRANS_FROM_Y = 0.0f;
    private static final float TRANS_TO_Y = 1.0f;
    private void performTranslateAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, TRANS_FROM_X,
                Animation.RELATIVE_TO_SELF, TRANS_TO_X,
                Animation.RELATIVE_TO_SELF, TRANS_FROM_Y,
                Animation.RELATIVE_TO_SELF, TRANS_TO_Y);
        animationSet.addAnimation(translateAnimation);
        translateAnimation.setDuration(DURATION);
        image.startAnimation(animationSet);
    }

    // scaling animation, x-axis from 100% to 10%, and
    // y-axis from 100% to 10% around the center of the
    // object. The animation start after 1s display.
    private static final float SCALE_FROM_X = 1.0f;
    private static final float SCALE_TO_X = 0.1f;
    private static final float SCALE_FROM_Y = 1.0f;
    private static final float SCALE_TO_Y = 0.1f;
    private static final float SCALE_CENTER_X = 0.5f;
    private static final float SCALE_CENTER_Y = 0.5f;
    private static final int START_OFFSET = 1000;   // 1s
    private void performScaleAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                SCALE_FROM_X, SCALE_TO_X, SCALE_FROM_Y, SCALE_TO_Y,
                Animation.RELATIVE_TO_SELF, SCALE_CENTER_X,
                Animation.RELATIVE_TO_SELF, SCALE_CENTER_Y);

        animationSet.addAnimation(scaleAnimation);

        animationSet.setStartOffset(START_OFFSET);

        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        animationSet.setDuration(DURATION);
        image.startAnimation(animationSet);
    }

    // rotation animation, from angle 0 to 360, around
    // 100% x-axis and 0% y-axis.
    private static final float ROTATE_ANGLE_FROM = 0f;
    private static final float ROTATE_ANGLE_TO = 360f;
    private static final float ROTATE_CENTER_X = 1f;
    private static final float ROTATE_CENTER_Y = 0f;
    private void performRotateAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        RotateAnimation rotateAnimation = new RotateAnimation(
                ROTATE_ANGLE_FROM, ROTATE_ANGLE_TO,
                Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_X,
                Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_Y);
        rotateAnimation.setDuration(DURATION);
        animationSet.addAnimation(rotateAnimation);
        image.startAnimation(animationSet);
    }

    // alpha animation that combines alpha transaction from
    // 100% to 0% and rotation from 0 to 360 around right-top,
    // repeat 4 times totally with an acceleration.
    private static final float ALPHA_FROM = 1.0f;
    private static final float ALPHA_TO = 0.0f;
    private static final int REPEAT_COUNT = 4;
    private void performAlphaAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new AccelerateInterpolator());
        AlphaAnimation alphaAnimation = new AlphaAnimation(ALPHA_FROM, ALPHA_TO);
        RotateAnimation rotateAnimation = new RotateAnimation(
                ROTATE_ANGLE_FROM, ROTATE_ANGLE_TO,
                Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_X,
                Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_Y);

        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(rotateAnimation);
        alphaAnimation.setDuration(DURATION);
        image.startAnimation(animationSet);
        animationSet.setRepeatCount(REPEAT_COUNT);
    }
}
