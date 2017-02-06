package image.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Inspector {
    private ArrayList<Traversal> traversals;
    private ImageData imgData;
    private CustomPixel[][] image;
    private int edgeThreshold;
    private LinkedHashMap<CustomPixel, String> pixelsToProcess;
    private boolean isShapeCounterInitialized;

    public Inspector(ArrayList<Traversal> traversals, ImageData imgData, int edgeThreshold) {
        this.traversals = traversals;
        this.imgData = imgData;
        this.image = ImageOperations.copyImageArray(imgData.getImage());
        this.edgeThreshold = edgeThreshold;
        this.pixelsToProcess = new LinkedHashMap<CustomPixel, String>();
        this.isShapeCounterInitialized = false;

        process();
    }

    public void addPixel(CustomPixel p, String direction) {
        pixelsToProcess.put(p, direction);
    }

    private void startNewTraversal(CustomPixel startPixel) {
        if (traversals.size() < Colors.NUMBER_OF_COLORS) {
            traversals.add(new Traversal(imgData, startPixel, edgeThreshold, traversals.size() + 1 + "", this));
        }
    }

    private int countActiveThreads() {
        int counter = 0;
        for (Traversal t : traversals) {
            if (t.getThread().isAlive()) {
                counter++;
            }
        }

        return counter;
    }

    public void process() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (traversals.size() >= Colors.NUMBER_OF_COLORS || (countActiveThreads() == 0 && traversals.size() > 0)) {
                        if (!isShapeCounterInitialized) {
                            new ShapeCounter(traversals, imgData, edgeThreshold);
                            isShapeCounterInitialized = true;
                        }
                    } else {
                        if (pixelsToProcess.size() > 0) {
                            Map.Entry<CustomPixel, String> entry = pixelsToProcess.entrySet().iterator().next();
                            pixelsToProcess.remove(entry.getKey());
                            CustomPixel startPixel = image[entry.getKey().getY()][entry.getKey().getX()];
                            boolean isStartPixelFound = true;

                            do {
                                if (entry.getValue().equals("north")) {
                                    if ((startPixel.getY() - 1) >= 0 && (startPixel.getY() - 1) < image.length &&
                                    startPixel.getX() >= 0 && startPixel.getX() < image[0].length) {
                                        startPixel = image[startPixel.getY() - 1][startPixel.getX()];
                                    } else {
                                        isStartPixelFound = false;
                                        break;
                                    }
                                } else if (entry.getValue().equals("south")) {
                                    if ((startPixel.getY() + 1) >= 0 && (startPixel.getY() + 1) < image.length &&
                                    startPixel.getX() >= 0 && startPixel.getX() < image[0].length) {
                                        startPixel = image[startPixel.getY() + 1][startPixel.getX()];
                                    } else {
                                        isStartPixelFound = false;
                                        break;
                                    }
                                } else if (entry.getValue().equals("east")) {
                                    if (startPixel.getY() >= 0 && startPixel.getY() < image.length &&
                                    (startPixel.getX() + 1) >= 0 && (startPixel.getX() + 1) < image[0].length) {
                                        startPixel = image[startPixel.getY()][startPixel.getX() + 1];
                                    } else {
                                        isStartPixelFound = false;
                                        break;
                                    }
                                } else if (entry.getValue().equals("west")) {
                                    if (startPixel.getY() >= 0 && startPixel.getY() < image.length &&
                                    (startPixel.getX() - 1) >= 0 && (startPixel.getX() - 1) < image[0].length) {
                                        startPixel = image[startPixel.getY()][startPixel.getX() - 1];
                                    } else {
                                        isStartPixelFound = false;
                                        break;
                                    }
                                }
                            } while (startPixel.getR() >= edgeThreshold);

                            if (isStartPixelFound && imgData.getTraversedInImagePixel(startPixel.getX(), startPixel.getY()) == 0) {
                                startNewTraversal(startPixel);
                            }
                        }
                    }

                    try {sleep(10);} catch (InterruptedException e2) { e2.printStackTrace(); }
                }
            }
        }.start();
    }
}
