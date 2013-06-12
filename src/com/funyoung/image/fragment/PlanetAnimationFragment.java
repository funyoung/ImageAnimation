package com.funyoung.image.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.Button;
import android.widget.ImageView;
import com.example.android.navigationdrawerexample.R;
import com.funyoung.image.animation.AnimationFactory;

import java.util.Locale;

/**
 * Fragment that appears in the "content_frame", shows a planet with animation
 */
public class PlanetAnimationFragment extends PlanetFragment {
    private static final String TAG = "PlanetAnimationFragment";
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


    private Button alphabutton = null;
    private Button scalebutton = null;
    private Button rotatebutton = null;
    private Button translatebutton = null;
    private AnimationFactory.ImageAnimation mAnimationEngine = null;
    protected void onRootViewCreated(View rootView) {
        alphabutton = (Button) rootView.findViewById(R.id.Alpha);
        scalebutton = (Button) rootView.findViewById(R.id.scale);
        rotatebutton = (Button) rootView.findViewById(R.id.rotate);
        translatebutton = (Button) rootView.findViewById(R.id.translate);

        ImageView image = (ImageView) rootView.findViewById(R.id.image);
        setOnClickListener(image);
    }

    private void setOnClickListener(final ImageView image) {
        // call this method to load animation from xml files.
        AnimationFactory.getInstance().setXmlEngine(getActivity());

        mAnimationEngine = AnimationFactory.getInstance().getCurrentAnimationEngine();
        if (null == mAnimationEngine) {
            Log.e(TAG, "setOnClickListener, invalid animation engine.");
        }

        if (null != alphabutton) {
            alphabutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image.startAnimation(mAnimationEngine.getAlphaAnimation());
                }
            });
        }

        if (null != rotatebutton) {
            rotatebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image.startAnimation(mAnimationEngine.getRotateAnimation());
                }
            });
        }

        if (null != scalebutton) {
            scalebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image.startAnimation(mAnimationEngine.getScaleAnimation());
                }
            });
        }

        if (null != translatebutton) {
            translatebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image.startAnimation(mAnimationEngine.getTranslateAnimation());
                }
            });
        }
    }
}
