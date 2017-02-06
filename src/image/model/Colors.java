package image.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Colors {
    private static final float GOLDEN_RATIO_CONJUGATE = 0.618033988749895f;
    public static final int NUMBER_OF_COLORS = 100;
    public static ArrayList<Color> colors = new ArrayList<Color>();

    public static void addRandomColors() {
        colors.clear();
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            float hue = new Random().nextFloat() + GOLDEN_RATIO_CONJUGATE;
            hue %= 1;
            colors.add(Color.getHSBColor(hue, 0.6f, 0.9f));
        }
    }
}
