package image.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DirectionChooser {
    private ArrayList<CustomPixel> possibleMoves;
    private CustomPixel[][] img;
    private int edgeThreshold;
    private Inspector inspector;
    private final int STEP_INTERVAL = 10000;
    private int stepLimit;

    public DirectionChooser(CustomPixel[][] img, int edgeThreshold, Inspector inspector) {
        this.possibleMoves = new ArrayList<CustomPixel>();
        this.img = img;
        this.edgeThreshold = edgeThreshold;
        this.inspector = inspector;
        this.stepLimit = 0;
    }

    public void clearPossibleMoves() {
        possibleMoves.clear();
    }

    private void filterAdd(int x, int y, String direction, int step) {
        if (x >= 0 && x < img[0].length && y >= 0 && y < img.length) {
            if (img[y][x].getR() < edgeThreshold) {
                possibleMoves.add(img[y][x]);
            } else {
                if (step > stepLimit) {
                    inspector.addPixel(img[y][x], direction);
                    stepLimit += STEP_INTERVAL;
                }
            }
        }
    }

    private int getNumberOfEqualTraversals() {
        int counter = 0;
        for (int i = 0; i < possibleMoves.size(); i++) {
            if (possibleMoves.get(i).getTraversed() == possibleMoves.get(0).getTraversed()) {
                counter++;
            }
        }

        return counter;
    }

    public CustomPixel getNextPos(CustomPixel p, int step) throws RandomWalkStuckException {
        filterAdd(p.getX(), p.getY() - 1, "north", step);
        filterAdd(p.getX(), p.getY() + 1, "south", step);
        filterAdd(p.getX() + 1, p.getY(), "east", step);
        filterAdd(p.getX() - 1, p.getY(), "west", step);

        Collections.sort(possibleMoves, new CustomPixelComparator());

        if (possibleMoves.size() > 0) {
            return possibleMoves.get(new Random().nextInt(getNumberOfEqualTraversals()));
        } else {
            throw new RandomWalkStuckException();
        }
    }
}
