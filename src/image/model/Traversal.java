package image.model;

import java.awt.*;
import java.util.*;

public class Traversal {
    private Thread thread;
    private ImageData imgData;
    private CustomPixel[][] image;
    private boolean isPauseTraversal;
    private boolean isStopTraversal;
    private int edgeThreshold;
    private CustomPixel currentPixel;
    private ArrayList<CustomPixel> traversedPixels;
    private int totalTraversals;
    private Color color;
    private DirectionChooser dirChooser;
    private int exploreCounter;
    private final int STEPS_TO_LIVE = 20000;
    private int sleepTime = 0;

    public Traversal(ImageData imgData, CustomPixel currentPixel, int edgeThreshold, String threadName, Inspector inspector) {
        this.currentPixel = currentPixel;
        this.edgeThreshold = edgeThreshold;
        this.imgData = imgData;
        this.image = imgData.getImage();
        this.traversedPixels = new ArrayList<CustomPixel>();
        this.totalTraversals = 0;
        this.isPauseTraversal = false;
        this.isStopTraversal = false;
        this.color = Colors.colors.remove(new Random().nextInt(Colors.colors.size()));
        this.dirChooser = new DirectionChooser(image, edgeThreshold, inspector);
        this.exploreCounter = 0;

        traverse(threadName);
    }

    public Thread getThread() {
        return thread;
    }

    public int getTotalTraversals() {
        return totalTraversals;
    }

    public void setStopTraversal(boolean stopTraversal) {
        this.isStopTraversal = stopTraversal;
    }

    public void setPauseTraversal(boolean pauseTraversal) {
        this.isPauseTraversal = pauseTraversal;
    }

    public ArrayList<CustomPixel> getTraversedPixels() {
        return traversedPixels;
    }

    private void traverse(String name) {
        thread = new Thread(name) {
            @Override
            public void run() {
                for (int i = 0; i < 10000000; i++) {
                    while (isPauseTraversal) {
                        try {sleep(200);} catch (InterruptedException e2) { e2.printStackTrace(); }
                    }

                    if (isStopTraversal) { return; }

                    if (exploreCounter >= STEPS_TO_LIVE) {
                        System.out.println(traversedPixels.size());
                        return;
                    }

                    imgData.incrementTraversedInImageWithRandomWalksPixel(currentPixel.getX(), currentPixel.getY());
                    exploreCounter++;

                    if (imgData.getTraversedInImagePixel(currentPixel.getX(), currentPixel.getY()) == 0) {
                        imgData.incrementTraversedInImagePixel(currentPixel.getX(), currentPixel.getY());
                        exploreCounter = 0;
                    }

                    if (!imgData.isColorInImageWithRandomWalksPixel(currentPixel.getX(), currentPixel.getY(), color)) {
                        imgData.setColorInImageWithRandomWalksPixel(currentPixel.getX(), currentPixel.getY(), color);
                    }

                    if (!traversedPixels.contains(currentPixel)) {
                        traversedPixels.add(currentPixel);
                    }

                    totalTraversals = i;

                    try {
                        currentPixel = dirChooser.getNextPos(currentPixel, i);
                    } catch (RandomWalkStuckException rwse) {
                        rwse.printStackTrace();
                        return;
                    }
                    dirChooser.clearPossibleMoves();

                    try {sleep(sleepTime);} catch (InterruptedException e2) { e2.printStackTrace(); }
                }
            }
        };
        thread.start();
    }
}
