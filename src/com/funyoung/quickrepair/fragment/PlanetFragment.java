package com.funyoung.quickrepair.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.funyoung.qcwx.R;

import java.util.Locale;

/**
 * Created by yangfeng on 13-8-10.
 */

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public  class PlanetFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    private View rootView;

    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        int i = getArguments().getInt(ARG_PLANET_NUMBER);
        update(i);
        return rootView;
    }

    public void update(int position) {
        String planet = getResources().getStringArray(R.array.planets_array)[position];

        int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                "drawable", getActivity().getPackageName());
        ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
        getActivity().setTitle(planet);
    }
}

