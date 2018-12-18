package com.coldradio.benzene.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import com.coldradio.benzene.util.Notifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCreator {
    private static Bitmap createBitmap(View view, RectF region) {
        if (region.width() * region.height() == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);

        // if region's x or y goes to negative, it throws IllegalArgumentException.
        region.intersect(0, 0, view.getWidth(), view.getHeight());

        return Bitmap.createBitmap(bitmap, (int) region.left, (int) region.top, (int) region.width(), (int) region.height());
    }

    public static void savePreview(View view, RectF region, String projectName) {
        File file = new File(ProjectFileManager.instance().projectFileRootDir() + projectName + ".png");

        try {
            Bitmap bitmap = createBitmap(view, region);

            if (bitmap == null) {
                Notifier.instance().notification("Preview can NOT be saved");
                return;
            }
            file.createNewFile();
            FileOutputStream oStream = new FileOutputStream(file, false);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
        } catch (IllegalArgumentException iae) {
            // create bitmap may throw this exception
            // do nothing
        }
    }

    public static void showPreview(ImageView imageView, String projectName) {
        File file = new File(ProjectFileManager.instance().projectFileRootDir() + projectName + ".png");

        if (file.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);
        }
    }
}