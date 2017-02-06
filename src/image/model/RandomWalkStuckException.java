package image.model;

public class RandomWalkStuckException extends Exception {
    public RandomWalkStuckException() {
        super("Random Walk is stuck. There are no possible moves available.");
    }
}
