package com.funyoung.image.animation;

import android.content.Context;
import android.view.animation.*;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by yangfeng on 13-6-12.
 */
public class AnimationFactory {
    private static AnimationFactory _instance;

    public static final int ENGINE_TYPE_DEFAULT = 0;
    public static final int ENGINE_TYPE_XML = 1;

    public static interface ImageAnimation {
        public Animation getTranslateAnimation();
        public Animation getScaleAnimation();
        public Animation getRotateAnimation();
        public Animation getAlphaAnimation();
    }

    private AnimationFactory() {
        // no instance outside this class.
    }
    public static AnimationFactory getInstance() {
        if (null == _instance) {
            _instance = new AnimationFactory();
            _instance.init();
        }
        return _instance;
    }

    public ImageAnimation getCurrentAnimationEngine() {
        return mCachedEngineMap.get(Integer.valueOf(mEngineType));
    }

    private int mEngineType;
    private HashMap<Integer, ImageAnimation> mCachedEngineMap = new HashMap<Integer, ImageAnimation>();
    private void init() {
        mEngineType = ENGINE_TYPE_DEFAULT;
        ImageAnimation engine = mCachedEngineMap.get(Integer.valueOf(mEngineType));
        if (null == engine) {
            engine = new DefaultImageAnimation();
            mCachedEngineMap.put(Integer.valueOf(mEngineType), engine);
        }
    }

    public void setXmlEngine(Context context) {
        mEngineType = ENGINE_TYPE_XML;

        ImageAnimation engine = mCachedEngineMap.get(Integer.valueOf(mEngineType));
        if (null == engine) {
            engine = new XmlImageAnimation(context);
            mCachedEngineMap.put(Integer.valueOf(mEngineType), engine);
        }
    }

    private static class DefaultImageAnimation implements ImageAnimation {

        private static final int DURATION = 5000;   // 5s

        // Translating animation, x-axis from 0% to 50%, and
        // y-axis from 0% to 100%.
        private static final float TRANS_FROM_X = 0.0f;
        private static final float TRANS_TO_X = 0.5f;
        private static final float TRANS_FROM_Y = 0.0f;
        private static final float TRANS_TO_Y = 1.0f;
        @Override
        public Animation getTranslateAnimation() {
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, TRANS_FROM_X,
                    Animation.RELATIVE_TO_SELF, TRANS_TO_X,
                    Animation.RELATIVE_TO_SELF, TRANS_FROM_Y,
                    Animation.RELATIVE_TO_SELF, TRANS_TO_Y);
            animationSet.addAnimation(translateAnimation);
            translateAnimation.setDuration(DURATION);
            return animationSet;
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
        @Override
        public Animation getScaleAnimation() {
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
            return animationSet;
        }

        // rotation animation, from angle 0 to 360, around
        // 100% x-axis and 0% y-axis.
        private static final float ROTATE_ANGLE_FROM = 0f;
        private static final float ROTATE_ANGLE_TO = 360f;
        private static final float ROTATE_CENTER_X = 1f;
        private static final float ROTATE_CENTER_Y = 0f;
        @Override
        public Animation getRotateAnimation() {
            AnimationSet animationSet = new AnimationSet(true);
            RotateAnimation rotateAnimation = new RotateAnimation(
                    ROTATE_ANGLE_FROM, ROTATE_ANGLE_TO,
                    Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_X,
                    Animation.RELATIVE_TO_PARENT, ROTATE_CENTER_Y);
            rotateAnimation.setDuration(DURATION);
            animationSet.addAnimation(rotateAnimation);
            return animationSet;
        }

        // alpha animation that combines alpha transaction from
        // 100% to 0% and rotation from 0 to 360 around right-top,
        // repeat 4 times totally with an acceleration.
        private static final float ALPHA_FROM = 1.0f;
        private static final float ALPHA_TO = 0.0f;
        private static final int REPEAT_COUNT = 4;
        @Override
        public Animation getAlphaAnimation() {
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
            animationSet.setRepeatCount(REPEAT_COUNT);
            return animationSet;
        }
    }

    private static class XmlImageAnimation implements ImageAnimation {
        /// four animation files named as alpha.xml, rotate.xml, scale.xml and translate.xml.
        private static final String XML_NAME_ALPHA = "alpha";
        private static final String XML_NAME_ROTATE = "rotate";
        private static final String XML_NAME_SCALE = "scale";
        private static final String XML_NAME_TRANSLATE = "translate";
        private int getAnimationId(String name) {
            int id = mContext.getResources().getIdentifier(name.toLowerCase(Locale.getDefault()),
                    "anim", mContext.getPackageName());
            return id;
        }

        private Context mContext;
        public XmlImageAnimation(Context context) {
            mContext = context;
        }

        @Override
        public Animation getTranslateAnimation() {
            return AnimationUtils.loadAnimation(mContext, getAnimationId(XML_NAME_TRANSLATE));
        }

        @Override
        public Animation getScaleAnimation() {
            return AnimationUtils.loadAnimation(mContext, getAnimationId(XML_NAME_SCALE));
        }

        @Override
        public Animation getRotateAnimation() {
            return AnimationUtils.loadAnimation(mContext, getAnimationId(XML_NAME_ROTATE));
        }

        @Override
        public Animation getAlphaAnimation() {
            return AnimationUtils.loadAnimation(mContext, getAnimationId(XML_NAME_ALPHA));
        }
    }
}
