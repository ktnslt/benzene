package com.coldradio.benzene.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.MediaStore;
import android.view.View;

import com.coldradio.benzene.project.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtil {
    private static Paint mBgPaint;

    private static String availableScreenShotFileName(String dir) {
        String prefix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());

        return FileUtil.availableFileName(dir, prefix, Configuration.IMAGE_FILE_EXT);
    }

    private static void addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + Configuration.IMAGE_FILE_EXT.substring(1));
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static boolean shotToFile(String filePath, View view, RectF rect, Context context) {
        Bitmap bitmap = createBitmap(view, rect);
        File file = new File(filePath);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Configuration.IMAGE_FORMAT, 100, out);
            out.flush();
            addImageToGallery(file.toString(), context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            FileUtil.closeIgnoreException(out);
        }
    }

    public static Bitmap createBitmap(View view, RectF region) {
        if (region.width() * region.height() == 0) {
            return null;
        }
        if (mBgPaint == null) {
            mBgPaint = new Paint();
            mBgPaint.setColor(Color.WHITE);
            mBgPaint.setStyle(Paint.Style.FILL);
        }
        Bitmap bitmap = Bitmap.createBitmap((int) region.width(), (int) region.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // fill the background
        canvas.drawRect(0, 0, region.width(), region.height(), mBgPaint);

        canvas.translate(-region.left, -region.top);
        view.draw(canvas);

        return bitmap;
    }

    public static void shotToGallery(View view, RectF rect, Context context) {
        if (!PermissionManager.instance().hasPermission(PermissionManager.PermissionCode.WRITE_EXTERNAL_STORAGE)) {
            Notifier.instance().notification("Permission WRITE_EXTERNAL_STORAGE was denied");
            return;
        }
        String dir = AppEnv.instance().screenShotDir();

        if (FileUtil.makeDirIfNotExist(dir)) {
            if (!shotToFile(dir + availableScreenShotFileName(dir), view, rect, context)) {
                Notifier.instance().notification("Saving Image Failed");
            }
        } else {
            Notifier.instance().notification("Direction Creation Failed");
        }
    }

    public static String shotToTemporary(View view, RectF rect, Context context) {
        String dir = AppEnv.instance().temporaryDir();

        if (FileUtil.makeDirIfNotExist(dir)) {
            String filePath = dir + availableScreenShotFileName(dir);

            if (shotToFile(filePath, view, rect, context)) {
                return filePath;
            }
        }
        return null;
    }
}
