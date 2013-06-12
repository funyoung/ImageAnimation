package com.funyoung.image.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        setAnimationButtons(rootView);
        return rootView;
    }

    private void setAnimationButtons(View rootView) {

    }
}
