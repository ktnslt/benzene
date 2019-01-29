package com.coldradio.benzene.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import com.coldradio.benzene.util.AppEnv;
import com.coldradio.benzene.util.FileUtil;
import com.coldradio.benzene.util.ImageUtil;
import com.coldradio.benzene.util.Notifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewHandler {
    public static boolean hasPreviewFile(String projectName) {
        return new File(AppEnv.instance().projectFileDir() + projectName + Configuration.IMAGE_FILE_EXT).exists();
    }

    public static Bitmap savePreview(View view, RectF region, String projectName) {
        File file = new File(AppEnv.instance().projectFileDir() + projectName + Configuration.IMAGE_FILE_EXT);
        FileOutputStream oStream = null;

        try {
            Bitmap bitmap = ImageUtil.createBitmap(view, region);

            if (bitmap != null) {
                file.createNewFile();
                oStream = new FileOutputStream(file, false);
                bitmap.compress(Configuration.IMAGE_FORMAT, 100, oStream);

                return bitmap;
            }
        } catch (IOException e) {
            // do nothing
        } catch (IllegalArgumentException iae) {
            // create bitmap may throw this exception
            // do nothing
        } finally {
            FileUtil.closeIgnoreException(oStream);
        }
        return null;
    }
}