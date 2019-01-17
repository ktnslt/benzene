package com.coldradio.benzene.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Date;

public class FileUtil {
    public static String INVALID_CHARS = "\\/:*?\"<>|&%";

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

    public static boolean rename(String sourcePath, String destPath) {
        return new File(sourcePath).renameTo(new File(destPath));
    }

    public static boolean delete(String filePath) {
        return new File(filePath).delete();
    }

    public static String availableFileName(String dirPath, String name, String postfix) {
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

    public static String nameWithoutExtension(String name, String ext) {
        if (name.endsWith(ext)) {
            return name.substring(0, name.length() - ext.length());
        } else {
            return name;
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
        } finally {
            closeIgnoreException(sourceChannel);
            closeIgnoreException(destChannel);
        }
        return false;
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
}
