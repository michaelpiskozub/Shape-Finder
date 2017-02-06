package image.model;

import java.util.Comparator;

public class CustomPixelComparator implements Comparator<CustomPixel> {
    public int compare(CustomPixel a, CustomPixel b) {
        int traversalComparison = Integer.valueOf(a.getTraversed()).compareTo(b.getTraversed());

        return traversalComparison == 0 ? Integer.valueOf(a.getR()).compareTo(b.getR()) : traversalComparison;
    }
}
