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
public class PlanetFragment extends ImageFragment {
    private static final String ARG_PLANET_NUMBER = "planet_number";

    protected PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    public static ImageFragment createInstance(int position) {
        ImageFragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    protected View getRootView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        return rootView;
    }

    protected String getImageTitle() {
        int i = getArguments().getInt(ARG_PLANET_NUMBER);
        String planet = getResources().getStringArray(R.array.planets_array)[i];
        return planet;
    }

    /// provide image via resource id.
    protected int getImageId() {
        String planet = getImageTitle();
        int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                RES_TYPE_DRAWABLE, getActivity().getPackageName());
        return imageId;
    }
}
