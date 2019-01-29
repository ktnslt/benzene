package com.coldradio.benzene.project;

import android.graphics.Bitmap;

public class Configuration {
    public static int BOND_THICKNESS = 4;
    public static int BOND_LENGTH = 120;
    public static float WEDGE_WIDTH_TO_BOND_LENGTH = 0.1f;
    public static float H_BOND_LENGTH_RATIO = 0.6f;
    public static int SELECT_RANGE = BOND_LENGTH / 3;
    public static int INITIAL_REGION_SIZE = 200;
    public static int ROTATION_PIVOT_SIZE = 40;
    public static int FONT_SIZE = 50;
    public static char ATOM_MARKER = '*';
    public static float SUBSCRIPT_SIZE_RATIO = 0.7f;
    public static float ELECTRON_RADIUS = 4f;
    public static float CHARGE_CIRCLE_RADIUS = 15f;
    public static int SELECTED_ATOM_BG_CIRCLE_RADIUS = 10;
    public static int MAX_HISTORY_LIST = 50;
    public static Bitmap.CompressFormat IMAGE_FORMAT = Bitmap.CompressFormat.PNG;
    public static String IMAGE_FILE_EXT = ".png";
    public static String PROJECT_FILE_EXT = ".bzn";
    public static int MAX_RESPONSE_FOR_SEARCH = 25;
    public static int REQUEST_IMAGE_QUALITY = 0;    // 0 for small, 1 for medium, 2 for large
    public static int MAX_SEARCH_HISTORY = 20;
}
