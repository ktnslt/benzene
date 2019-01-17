package com.coldradio.benzene.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import com.coldradio.benzene.util.Environment;
import com.coldradio.benzene.util.ImageUtil;
import com.coldradio.benzene.util.Notifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewHandler {
    public static void savePreview(View view, RectF region, String projectName) {
        File file = new File(Environment.instance().projectFilePath() + projectName + Configuration.IMAGE_FILE_EXT);

        try {
            Bitmap bitmap = ImageUtil.createBitmap(view, region);

            if (bitmap == null) {
                Notifier.instance().notification("Preview can NOT be saved");
                return;
            }
            file.createNewFile();
            FileOutputStream oStream = new FileOutputStream(file, false);

            bitmap.compress(Configuration.IMAGE_FORMAT, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
        } catch (IllegalArgumentException iae) {
            // create bitmap may throw this exception
            // do nothing
        }
    }

    public static void showPreview(ImageView imageView, String projectName) {
        File file = new File(Environment.instance().projectFilePath() + projectName + Configuration.IMAGE_FILE_EXT);

        if (file.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);
        }
    }
}