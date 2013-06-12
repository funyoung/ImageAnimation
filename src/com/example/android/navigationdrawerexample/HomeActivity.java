package com.example.android.navigationdrawerexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by yangfeng on 13-6-12.
 */
public class HomeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CoverFlow cf = new CoverFlow(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        cf.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg));//背景
        cf.setAdapter(new ImageAdapter(this));
        ImageAdapter imageAdapter = new ImageAdapter(this);
        cf.setAdapter(imageAdapter);
        cf.setSelection(2, true);
        cf.setAnimationDuration(1000);
        setContentView(cf);
    }

    public static class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        //加载资源图片
        private Integer[] mImageIds = {
                R.drawable.mercury,
                R.drawable.venus,
                R.drawable.earth,
                R.drawable.mars,
                R.drawable.jupiter,
                R.drawable.saturn,
                R.drawable.uranus,
                R.drawable.neptune,
                };

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = createReflectedImages(mContext, mImageIds[position]);
            i.setLayoutParams(new CoverFlow.LayoutParams(120, 100));
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            // 设置的抗锯齿
            BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
            drawable.setAntiAlias(true);
            return i;
        }

        public float getScale(boolean focused, int offset) {
            return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
        }

        /**
         * 068
         * 设置镜像图像
         * 069
         *
         * @param mContext 070
         * @param imageId  071
         *                 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
         */
        public ImageView createReflectedImages(Context mContext, int imageId) {
            Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
            final int reflectionGap = 4;
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);

            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    height / 2, width, height / 2, matrix, false);

            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmapWithReflection);

            canvas.drawBitmap(originalImage, 0, 0, null);

            Paint deafaultPaint = new Paint();
            canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, originalImage
                    .getHeight(), 0, bitmapWithReflection.getHeight()
                    + reflectionGap, 0x70ffffff, 0x00ffffff, Shader.TileMode.MIRROR);

            paint.setShader(shader);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(bitmapWithReflection);

            return imageView;
        }
    }

    public static class CoverFlow extends Gallery {
        private Camera mCamera = new Camera();
        private int mMaxRotationAngle = 50;//60;
        private int mMaxZoom = -380;//-120;
        private int mCoveflowCenter;
        private boolean mAlphaMode = true;
        private boolean mCircleMode = false;

        public CoverFlow(Context context) {
            super(context);
            this.setStaticTransformationsEnabled(true);
        }

        public CoverFlow(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setStaticTransformationsEnabled(true);
        }

        public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.setStaticTransformationsEnabled(true);
        }

        public int getMaxRotationAngle() {
            return mMaxRotationAngle;
        }

        public void setMaxRotationAngle(int maxRotationAngle) {
            mMaxRotationAngle = maxRotationAngle;
        }

        public boolean getCircleMode() {
            return mCircleMode;
        }

        public void setCircleMode(boolean isCircle) {
            mCircleMode = isCircle;
        }

        public boolean getAlphaMode() {
            return mAlphaMode;
        }

        public void setAlphaMode(boolean isAlpha) {
            mAlphaMode = isAlpha;
        }

        public int getMaxZoom() {
            return mMaxZoom;
        }

        public void setMaxZoom(int maxZoom) {
            mMaxZoom = maxZoom;
        }

        private int getCenterOfCoverflow() {
            return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                    + getPaddingLeft();
        }

        private static int getCenterOfView(View view) {
            return view.getLeft() + view.getWidth() / 2;
        }

        protected boolean getChildStaticTransformation(View child, Transformation t) {
            final int childCenter = getCenterOfView(child);
            final int childWidth = child.getWidth();
            int rotationAngle = 0;
            t.clear();
            t.setTransformationType(Transformation.TYPE_MATRIX);
            if (childCenter == mCoveflowCenter) {
                transformImageBitmap((ImageView) child, t, 0);
            } else {
                rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
                if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                    rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
                    : mMaxRotationAngle;
                }
                transformImageBitmap((ImageView) child, t, rotationAngle);
            }
            return true;
        }

                /**
                 097
                 *
                 098
                 */
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
        }

                /**
                 105
                 * 把图像位图的角度通过
                 106
                 */
        private void transformImageBitmap(ImageView child, Transformation t,
                                          int rotationAngle) {
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);
            mCamera.translate(0.0f, 0.0f, 100.0f);

            // 如视图的角度更少,放大
            if (rotation <= mMaxRotationAngle) {
                float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                mCamera.translate(0.0f, 0.0f, zoomAmount);
                if (mCircleMode) {
                    if (rotation < 40)
                    mCamera.translate(0.0f, 155, 0.0f);
                    else
                    mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
                }
                if (mAlphaMode) {
                    ((ImageView) (child)).setAlpha((int) (255 - rotation * 2.5));
                }
            }
            mCamera.rotateY(rotationAngle);
            mCamera.getMatrix(imageMatrix);
            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();
        }
    }
}
