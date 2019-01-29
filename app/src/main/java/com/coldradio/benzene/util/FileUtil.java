package com.coldradio.benzene.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.coldradio.benzene.project.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Date;

public class FileUtil {
    public static String INVALID_CHARS = "\\/:*?\"<>|&%";
    private static int BUF_SIZE = 1024;

    private static String prefixFromName(String name) {
        if (name.endsWith(")")) {
            int varStartInd = name.lastIndexOf('(');

            for (int ii = varStartInd + 1; ii < name.length() - 1; ii++) {
                if (!Character.isDigit(name.charAt(ii))) {
                    return name;
                }
            }
            return name.substring(0, varStartInd);
        }
        return name;
    }

    public static void closeIgnoreException(FileChannel fc) {
        if (fc != null) {
            try {
                fc.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeIgnoreException(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeIgnoreException(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeIgnoreException(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeIgnoreException(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean rename(String sourcePath, String destPath) {
        return new File(sourcePath).renameTo(new File(destPath));
    }

    public static boolean delete(String filePath) {
        return new File(filePath).delete();
    }

    public static String availableFileName(String name) {
        return fileNameWithoutExt(availableFileNameExt(AppEnv.instance().projectFileDir(), name, Configuration.PROJECT_FILE_EXT));
    }

    public static String availableFileNameExt(String dirPath, String name, String postfix) {
        String prefix = prefixFromName(name);
        File candidate = new File(dirPath, prefix + postfix);

        if (name.equals(prefix) && !candidate.exists()) {
            return candidate.getName();
        }

        for (int ii = 1; ; ++ii) {
            candidate = new File(dirPath, prefix + "(" + ii + ")" + postfix);
            if (!candidate.exists()) {
                return candidate.getName();
            }
        }
    }

    public static String fileNameWithoutExt(String name) {
        int dot_index = name.lastIndexOf('.');

        if (dot_index < 0) {
            return name;
        }
        return name.substring(0, dot_index);
    }

    public static boolean copy(Uri sourceUri, String destPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = AppEnv.instance().getApplicationContext().getContentResolver().openInputStream(sourceUri);
            outputStream = new FileOutputStream(destPath);
            byte[] bytes = new byte[BUF_SIZE];

            while (true) {
                int count = inputStream.read(bytes, 0, BUF_SIZE);
                if (count == -1)
                    break;
                outputStream.write(bytes, 0, count);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            closeIgnoreException(inputStream);
            closeIgnoreException(outputStream);
        }
    }

    public static boolean copy(String sourcePath, String destPath) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourcePath).getChannel();
            destChannel = new FileOutputStream(destPath).getChannel();

            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

            return true;
        } catch (Exception e) {
            return false;
        } finally {
            closeIgnoreException(sourceChannel);
            closeIgnoreException(destChannel);
        }
    }

    public static boolean validFileName(String name) {
        if (name.equals(".") || name.equals("..")) {
            return false;
        } else {
            for (int ii = 0; ii < name.length(); ++ii) {
                if (INVALID_CHARS.indexOf(name.charAt(ii)) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static long lastModifiedTime(String filePath) {
        File file = new File(filePath);

        if (file.exists())
            return file.lastModified();
        return 0;
    }

    public static String toDateTimeString(long timeInMs) {
        Date date = new Date();

        date.setTime(timeInMs);
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(date);
    }

    public static boolean exists(String fileName) {
        return new File(AppEnv.instance().projectFileDir(), fileName + Configuration.PROJECT_FILE_EXT).exists();
    }

    public static boolean share(String filePath, Activity activity) {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            File file = new File(filePath);

            if (!file.exists()) {
                return false;
            }

            if (filePath.endsWith(Configuration.IMAGE_FILE_EXT)) {
                share.setType("image/" + Configuration.IMAGE_FILE_EXT.substring(1));
            } else {
                share.setType("text/xml");
            }

            Uri uri = FileProvider.getUriForFile(activity, "com.coldradio.benzene.fileprovider", file);

            share.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(share, "Share to Others"));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean makeDirIfNotExist(String path) {
        File dir = new File(path);

        if (dir.exists() || dir.mkdirs()) {
            return true;
        }
        return false;
    }

    public static void removeAllFilesInDir(String path) {
        File dirFile = new File(path);
        File[] files = dirFile.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public static void browseFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        AppEnv.instance().getCurrentActivity().startActivityForResult(intent, requestCode);
    }

    public static String fileNameExt(String filePath) {
        int cut = filePath.lastIndexOf('/');

        if (cut < 0) {
            return filePath;
        }
        return filePath.substring(cut + 1);
    }

    public static boolean isValidProjectFile(Uri uri) {
        InputStream inputStream = null;

        try {
            inputStream = AppEnv.instance().getApplicationContext().getContentResolver().openInputStream(uri);
            byte[] bytes = new byte[100];
            int count = inputStream.read(bytes, 0, 100);

            if (count == -1) {
                return false;
            }

            String firstLine = new String(bytes);

            if (firstLine.contains("Atom") || firstLine.contains("AID")) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        } finally {
            closeIgnoreException(inputStream);
        }
    }

    public static Bitmap loadBitmap(String fileName) {
        File file = new File(AppEnv.instance().projectFileDir() + fileName + Configuration.IMAGE_FILE_EXT);
        Bitmap bitmap = null;

        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }

        return bitmap;
    }
}
