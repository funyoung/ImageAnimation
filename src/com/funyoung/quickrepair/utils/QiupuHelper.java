package com.funyoung.quickrepair.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.ssl.AbstractVerifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static android.os.Environment.MEDIA_MOUNTED;

public class QiupuHelper {
    private static final String TAG = "QiupuHelper";


    private static ImageCacheManager cachemanager;
//    private static String tmpPath = MarketConfiguration.getCacheDirctory().getPath() + File.separator;
 
    static StatFs stat = null;

    public static int mVersionCode;
    private static HashMap<String,Long> downloadingMap;

    static {
        downloadingMap = new HashMap<String,Long>();
        cachemanager = ImageCacheManager.instance();
    }
    

    public static String isImageExistInPhone(Context context, String url, boolean addHostAndPath) {
        String localpath = null;
        try {
            final URL imageurl = new URL(url);
            final String filepath = getImageFilePath(context, imageurl, addHostAndPath);
            final File file = new File(filepath);
            if (file.exists() == true && file.length() > 0) {
                localpath = filepath;
            } else if (url.endsWith(".icon.png")) {
                final String alterFilePath = getAlterLocalImageFilePath(context,
                        imageurl, addHostAndPath);
                final File alterFile = new File(alterFilePath);
                if (alterFile.exists() && alterFile.length() > 0) {
                    localpath = alterFilePath;
                }
            }
        } catch (MalformedURLException ne) {
            Log.d(TAG, "isImageExistInPhone exception=" + ne.getMessage()
                    + " url=" + url);
        }
        return localpath;

    }

    public static class myHostnameVerifier extends AbstractVerifier {
        public myHostnameVerifier() {

        }

        public final void verify(final String host, final String[] cns,
                final String[] subjectAlts) {
            Log.d(TAG, "host =" + host);
        }
    }

    public static String getImagePathFromURL(Context con, String url,
            boolean addHostAndPath) {
        if (url == null || url.length() == 0)
            return null;

        try {
            URL imageUrl = new URL(url);
            String filePath = getImageFilePath(con, imageUrl, addHostAndPath);

            File file = new File(filePath);
            if (file.exists() == false || file.length() == 0) {
                if (file.exists() == true) {
                    file.delete();
                }

                File savedFile = createTempPackageFile(con, filePath);
                if (!downloadImageFromInternet(imageUrl, savedFile)) {
                    file.delete();
                    return null;
                }
            }
            return filePath;
        } catch (MalformedURLException ne) {
            Log.e(TAG,
                    "getImageFromURL url=" + url + " exception="
                            + ne.getMessage());
            return null;
        }
    }

    private static String getImageFileName(String filename) {
        if (filename.contains("=") || filename.contains("?")
                || filename.contains("&") || filename.contains("%")) {
            filename = filename.replace("?", "");
            filename = filename.replace("=", "");
            filename = filename.replace("&", "");
            filename = filename.replace("%", "");
        }

        return filename;
    }

    /*
     * photos-a.ak.fbcdn.net api.facebook.com secure-profile.facebook.com
     * ssl.facebook.com www.facebook.com x.facebook.com api-video.facebook.com
     * developers.facebook.com iphone.facebook.com developer.facebook.com
     * m.facebook.com s-static.ak.facebook.com secure-profile.facebook.com
     * secure-media-sf2p.facebook.com ssl.facebook.com profile.ak.facebook.com
     * b.static.ak.facebook.com
     * 
     * photos-h.ak.fbcdn.net photos-f.ak.fbcdn.net
     */
    private static boolean isInTrustHost(String host) {
        if (host.contains(".fbcdn.net"))
            return true;

        if (host.contains("secure-profile.facebook.com"))
            return true;

        return false;
    }

    public static String getImageFilePath(Context context, URL imageUrl, boolean addHostAndPath) {
        return getImageFilePath(getCachedPath(context) + File.separator, imageUrl, addHostAndPath);
    }

    public static String getImageFilePath(String path, URL imageUrl, boolean addHostAndPath) {
        if (addHostAndPath == false) {
            return path
                    + new File(getImageFileName(imageUrl.getFile())).getName();
        } else {
            String filename = imageUrl.getFile();
            filename = removeChar(filename);

            String host = imageUrl.getHost();
            if (isInTrustHost(host) == false) {

                filename = host + "_" + filename;
                if (filename.contains("/")) {
                    filename = filename.replace("/", "");
                }
            } else {
                Log.d(TAG, "***********   i am in trust=" + host + " filename="
                        + filename);
            }

            return path + new File(filename).getName();
        }
    }

    private static String removeChar(String filename) {
        if (filename.contains("=") || filename.contains("?")
                || filename.contains("&") || filename.contains("%")) {
            filename = filename.replace("?", "");
            filename = filename.replace("=", "");
            filename = filename.replace("&", "");
            filename = filename.replace("%", "");
            filename = filename.replace(",", "");
            filename = filename.replace(".", "");
            filename = filename.replace("-", "");

        }
        return filename;
    }

    private static Bitmap getImageFromPath(String filePath, int sampleSize, int maxNumOfPixels) {
        if (filePath != null) {
            Bitmap tmp = null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            
//            opts.inSampleSize = ImageRun.computeSampleSizeLarger(
//                    opts.outWidth, opts.outHeight, sampleSize);
            opts.inSampleSize = ImageRun.computeSampleSize(
                    opts, sampleSize, maxNumOfPixels);
            opts.inJustDecodeBounds = false;
            try {
                tmp = BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError oof) {
                
            }
            return tmp;
        } else
            return null;
    }


    public static Bitmap getImageFromURL(Context con, String url,
            boolean isHighPriority, boolean addHostAndPath,
            boolean setRoundAngle, int width ,int maxNumOfPixels) {
        ImageCacheManager.ImageCache cache = cachemanager.getCache(url);
        if (cache == null || cache.bmp == null || cache.bmp.isRecycled()) {
            final Bitmap image;
            String filePath = getImagePathFromURL(con, url, addHostAndPath);
            image = getImageFromPath(filePath, width, maxNumOfPixels);

            if (image != null) {
                cachemanager.addCache(url, image);
            }
            return image;
        } else {
            return cache.bmp;
        }
    }

    static File createTempPackageFile(Context con, String filePath) {
        File tmpPackageFile;
        if (filePath.startsWith(getSdcardPath())) {
            tmpPackageFile = new File(filePath);
            return tmpPackageFile;
        }

        int i = filePath.lastIndexOf("/");
        String tmpFileName;
        if (i != -1) {
            tmpFileName = filePath.substring(i + 1);
        } else {
            tmpFileName = filePath;
        }
        FileOutputStream fos;
        try {
            fos = con.openFileOutput(tmpFileName, 1 | 2);
        } catch (FileNotFoundException e1) {
            Log.e(TAG, "Error opening file " + tmpFileName);
            return null;
        }
        try {
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error opening file " + tmpFileName);
            return null;
        }
        tmpPackageFile = con.getFileStreamPath(tmpFileName);
        return tmpPackageFile;
    }

    public static void createAllWritableFolder(String pathName) {
        if (TextUtils.isEmpty(pathName)) {
            Log.d(TAG, "createAllWritableFolder, invalid path name: "
                    + pathName);
            return;
        }

        File pathFile = new File(pathName);
        if (!pathFile.exists()) {
            pathFile.mkdirs();

            Process p;
            int status;
            try {
                p = Runtime.getRuntime().exec("chmod 777 " + pathName);
                status = p.waitFor();
                if (status == 0) {
                    Log.i(TAG, "createAllWritableFolder, chmod succeed, "
                            + pathName);
                } else {
                    Log.i(TAG, "createAllWritableFolder, chmod failed, "
                            + pathName);
                }
            } catch (Exception ex) {
                Log.i(TAG, "createAllWritableFolder, chmod exception, "
                        + pathName);
                ex.printStackTrace();
            }
        }
    }

    private static String getAlterLocalImageFilePath(Context context, URL imageurl,
            boolean addHostAndPath) {
        final String alterPath;

        final String imageFilePath = imageurl.getFile();
        final int subStart = imageFilePath.lastIndexOf('/');
        final int subEnd = imageFilePath.indexOf('-', subStart);
        final String localFileName;
        if (subEnd > 0) {
            localFileName = imageFilePath.substring(subStart + 1, subEnd)
                    + ".png";
        } else {
            localFileName = imageFilePath.substring(subStart + 1) + ".png";
        }

        Log.d(TAG, "getAlterLocalImageFilePath, get localFileName:"
                + localFileName + ", from:" + imageFilePath);
        if (addHostAndPath == false) {
            alterPath = getCachedPath(context) + File.separator
                    + new File(getImageFileName(localFileName)).getName();
        } else {
            String filename = removeChar(localFileName);
            filename = "local_" + filename;
            if (filename.contains("/")) {
                filename = filename.replace("/", "");
            }
            alterPath = getCachedPath(context) + File.separator + new File(filename).getName();
        }

        Log.v(TAG, "getAlterLocalImageFilePath, return alter local path:"
                + alterPath);
        return alterPath;
    }



    private static boolean downloadImageFromInternet(URL imageUrl, File filep) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            TrafficStats.setThreadStatsTag(0xB0AC);
        // get bitmap
        HttpURLConnection conn = null;
        FileOutputStream fos = null;
        try {
            String filepath = filep.getAbsolutePath();

            fos = new FileOutputStream(filep);

            conn = (HttpURLConnection) imageUrl.openConnection();

            if (HttpsURLConnection.class.isInstance(conn)) {
                myHostnameVerifier passv = new myHostnameVerifier();
                ((HttpsURLConnection) conn).setHostnameVerifier(passv);
            }

            conn.setConnectTimeout(15 * 1000);
            conn.setReadTimeout(30 * 1000);
            InputStream in = conn.getInputStream();

            int retcode = conn.getResponseCode();
            if (retcode == 200) {
                final long totalLength = conn.getContentLength();

                long downlen = 0;
                int len = -1;
                byte[] buf = new byte[1024 * 4];
                while ((len = in.read(buf, 0, 1024 * 4)) > 0) {
                    downlen += len;
                    fos.write(buf, 0, len);
                }
                buf = null;

                if (totalLength == downlen) {
                    Log.d(TAG, "downloadImageFromInternet, to file: " + filepath);
                } else {

                }
            }

            fos.close();
            Log.d(TAG, "downloadImageFromInternet, to file: " + filepath);
        } catch (IOException ne) {
            Log.e(TAG, "fail to get image=" + ne.getMessage());
            return false;
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                TrafficStats.clearThreadStatsTag();
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ne) {
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ne) {
                }
            }
        }

        return true;
    }

    private static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_CACHE_DIR_NAME = "image_cache";
    private static final String INDIVIDUAL_MARKET_DIR_NAME = "market";
    public static File getCacheDirectory(Context context) {
        File cacheDirctory = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && hasExternalStoragePermission(context)) {
            cacheDirctory = context.getExternalCacheDir();
        }

        if(cacheDirctory == null) {
            cacheDirctory = context.getCacheDir();
        }

        if(cacheDirctory == null) {
            Log.w(TAG, "Can't define system cache directory!");
            //try agin
            cacheDirctory = context.getCacheDir();
        }
        return cacheDirctory;
    }

    public static boolean hasExternalStoragePermission(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(MEDIA_MOUNTED) &&
                hasExternalStoragePermission(context)) {
            appCacheDir = new File(cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }
    private static File getIndividualMarketDirectory(Context context,String ownCacheDir) {
        File cacheDir;
        if(TextUtils.isEmpty(ownCacheDir)) {
            cacheDir = getCacheDirectory(context);
        }else {
            cacheDir = getOwnCacheDirectory(context, ownCacheDir);
        }
        File individualCacheDir = new File(cacheDir + File.separator + INDIVIDUAL_MARKET_DIR_NAME);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdirs()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }
    private static File getIndividualCacheDirectory(Context context,String ownCacheDir) {
        File cacheDir = getIndividualMarketDirectory(context,ownCacheDir);
        File individualCacheDir = new File(cacheDir, INDIVIDUAL_CACHE_DIR_NAME);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdirs()) {
                individualCacheDir = cacheDir;
            }
        }
        return individualCacheDir;
    }

    private static String getCachedPath(Context context) {
        if(cacheDirectory == null) {
            cacheDirectory = getIndividualCacheDirectory(context, null);
        }else {
            if(!cacheDirectory.exists()) {
                cacheDirectory.mkdirs();
            }
        }
        return cacheDirectory.getPath();
//        return MarketConfiguration.getCacheDirctory().getPath();
    }

    private static File cacheDirectory;

    public static File getTempAvatarFile(Context context) {
        return new File(getCachedPath(context) + File.separator + ".avatar_tmp");
    }
}
