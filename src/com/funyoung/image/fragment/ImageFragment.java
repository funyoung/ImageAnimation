package com.funyoung.image.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.android.navigationdrawerexample.R;

import java.util.Locale;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
abstract public class ImageFragment extends Fragment {
    protected static final String RES_TYPE_DRAWABLE = "drawable";
    private static final String TAG = "ImageFragment";

    protected ImageFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = getRootView(inflater, container, savedInstanceState);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.image);
        if (null == imageView) {
            Log.e(TAG, "onCreateView, no 'image' id in the view.");
        } else {
            onRootViewCreated(rootView);
            String title = getImageTitle();
            if (null != title) {
                getActivity().setTitle(title);
            }

            if (!setImageView(imageView)) {
                Log.e(TAG, "onCreateView, failed to set image for view.");
            }
        }
        return rootView;
    }

    private boolean setImageView(ImageView imageView) {
        int imageId = getImageId();
        if (imageId > 0) {
            imageView.setImageResource(imageId);
            return true;
        }

        Uri uri = getImageUri();
        if (null != uri) {
            imageView.setImageURI(uri);
            return true;
        }

        Drawable drawable = getImageDrawable();
        if (null != drawable) {
            imageView.setImageDrawable(drawable);
            return true;
        }

        Bitmap bmp = getImageBitmap();
        if (null != bmp) {
            imageView.setImageBitmap(bmp);
            return true;
        }

        return false;
    }

    abstract protected View getRootView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState);

    abstract protected String getImageTitle();

    /// sub class could provide image data via resource Id, or URI, or
    // drawable, or bitmap by implement one of bellow get-methods, if
    // multiple methods was implement, the first one will be used and
    // ignore others.
    protected int getImageId() {
        return -1;
    }
    protected Uri getImageUri() {
        return null;
    }
    protected Drawable getImageDrawable() {
        return null;
    }
    protected Bitmap getImageBitmap() {
        return null;
    }
    /// get-methods end.
    protected void onRootViewCreated(View rootView) {
    }
}
