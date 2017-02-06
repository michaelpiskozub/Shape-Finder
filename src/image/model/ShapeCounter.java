package image.model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ShapeCounter {
    private ArrayList<Traversal> traversals;
    private List<List<CustomPixel>> shapes;
    private ImageData imgData;
    private int edgeThreshold;

    public ShapeCounter(ArrayList<Traversal> traversals, ImageData imgData, int edgeThreshold) {
        this.traversals = traversals;
        this.shapes = new ArrayList<List<CustomPixel>>();
        this.imgData = imgData;
        this.edgeThreshold = edgeThreshold;

        countShapes();
    }

    private void addNewPath(ArrayList<CustomPixel> pixels) {
        for (int j = 0; j < shapes.size(); j++) {
            List<CustomPixel> l = shapes.get(j);
            for (CustomPixel p : l) {
                for (CustomPixel i : pixels) {
                    if (i.getX() == p.getX() && i.getY() == p.getY()) {
                        l.addAll(pixels);
                        return;
                    }
                }
            }
        }
        shapes.add(pixels);
    }

    private void countShapes() {
        int traversedPixels = 0;
        for (Traversal t : traversals) {
            traversedPixels += t.getTotalTraversals();
            addNewPath(t.getTraversedPixels());
        }

        int totalPixels = imgData.getImageHeight() * imgData.getImageWidth();
        int traversablePixels = ImageOperations.countTraversablePixels(imgData.getImage(), edgeThreshold);
        int notTraversedTraversablePixels = ImageOperations.countNotTraversedTraversablePixels(imgData.getImage(), edgeThreshold);
        int traversedTraversablePixels = traversablePixels - notTraversedTraversablePixels;

        String outputMessage = "In this image:\n" + totalPixels + " pixels\n" + traversablePixels +
                " traversable pixels\n" + traversedTraversablePixels + " traversed traversable pixels\n" +
                notTraversedTraversablePixels + " not traversed traversable pixels\n" +
                traversedPixels + " pixel traversals by " + traversals.size() + " random walks\n" +
                shapes.size() + " shapes";

        System.out.println(outputMessage);
        JOptionPane.showMessageDialog(null, outputMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}
