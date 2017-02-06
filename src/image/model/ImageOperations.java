package image.model;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ImageOperations {
    public static CustomPixel[][] robertsCrossOperator(CustomPixel[][] image, String colorMode, String projectionType) {
        double robertsCrossOperatorX[][] = {
                { 0,   0,  0},
                { 0,   0,  1},
                { 0,  -1,  0}
        };
        double robertsCrossOperatorY[][] = {
                { 0,  0,  0},
                { 0,  1,  0},
                { 0,  0, -1}
        };
        // z, y, x
        double robertsCross[][][] = {robertsCrossOperatorX, robertsCrossOperatorY};
        double robertsCrossConstants[] = {1, 1};

        return customOperator(image, robertsCross, robertsCrossConstants, projectionType, colorMode);
    }

    public static CustomPixel[][] sobelOperator(CustomPixel[][] image, String colorMode, String projectionType) {
        double sobelOperatorX[][] = {
                { 1,  0, -1},
                { 2,  0, -2},
                { 1,  0, -1}
        };
        double sobelOperatorY[][] = {
                { 1,  2,  1},
                { 0,  0,  0},
                {-1, -2, -1}
        };
        // z, y, x
        double sobel[][][] = {sobelOperatorX, sobelOperatorY};
        double sobelConstants[] = {1, 1};

        return customOperator(image, sobel, sobelConstants, projectionType, colorMode);
    }

    public static CustomPixel[][] prewittOperator(CustomPixel[][] image, String colorMode, String projectionType) {
        double prewittOperatorX[][] = {
                { 1,  0, -1},
                { 1,  0, -1},
                { 1,  0, -1}
        };
        double prewittOperatorY[][] = {
                { 1,  1,  1},
                { 0,  0,  0},
                {-1, -1, -1}
        };
        // z, y, x
        double prewitt[][][] = {prewittOperatorX, prewittOperatorY};
        double prewittConstants[] = {1, 1};

        return customOperator(image, prewitt, prewittConstants, projectionType, colorMode);
    }

    public static CustomPixel[][] scharrOperator(CustomPixel[][] image, String colorMode, String projectionType) {
        double scharrOperatorX[][] = {
                { 3,   0,  -3},
                {10,   0, -10},
                { 3,   0,  -3}
        };
        double scharrOperatorY[][] = {
                { 3,  10,  3},
                { 0,   0,  0},
                {-3, -10, -3}
        };
        // z, y, x
        double scharr[][][] = {scharrOperatorX, scharrOperatorY};
        double scharrConstants[] = {1, 1};

        return customOperator(image, scharr, scharrConstants, projectionType, colorMode);
    }

    public static CustomPixel[][] copyImageArray(CustomPixel[][] img) {
        CustomPixel[][] arrayCopy = new CustomPixel[img.length][img[0].length];
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {
                CustomPixel pix = img[i][j];
                arrayCopy[i][j] = new CustomPixel(pix.getX(), pix.getY(), pix.getR(), pix.getG(), pix.getB(), pix.getTraversed());
            }
        }
        return arrayCopy;
    }

    public static CustomPixel copyPixel(CustomPixel p) {
        return new CustomPixel(p.getX(), p.getY(), p.getR(), p.getG(), p.getB(), p.getTraversed());
    }

    private static int inverseTan(ArrayList<Double> values) {
        double result = Math.atan2(values.get(1), values.get(0));
        result += Math.PI;
        result *= 40.0;
        result = Math.round(result);
        return (int) result;
    }

    private static int rootOfSumOfSquares(ArrayList<Double> values) {
        double sumOfSquares = 0;
        for (double val : values) {
            sumOfSquares += (val * val);
        }
        double e = Math.sqrt(sumOfSquares);
        int value = (int) e;
        return clamp(value);
    }

    private static int clamp(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }
        return value;
    }

    private static CustomPixel[][] customOperator(CustomPixel[][] img, double[][][] masks, double[] masksConstants,
                                                  String projection, String colorMode) {
        CustomPixel[][] imageCopy = copyImageArray(img);

        final int FILTER_OFFSET_X = masks[0][0].length / 2;
        final int FILTER_OFFSET_Y = masks[0].length / 2;

        for (int ix = 0; ix < imageCopy[0].length; ix++) {
            for (int iy = 0; iy < imageCopy.length; iy++) {

                ArrayList<Double> redValues = new ArrayList<Double>();
                ArrayList<Double> greenValues = new ArrayList<Double>();
                ArrayList<Double> blueValues = new ArrayList<Double>();
                for (int mz = 0; mz < masks.length; mz++) {
                    double redVal = 0;
                    double greenVal = 0;
                    double blueVal = 0;
                    for (int mx = 0; mx < masks[0][0].length; mx++) {
                        for (int my = 0; my < masks[0].length; my++) {
                            int tempY = iy+(-FILTER_OFFSET_Y+my);
                            if (tempY < 0) {
                                tempY = imageCopy.length + tempY;
                            } else if (tempY >= imageCopy.length) {
                                tempY %= imageCopy.length;
                            }
                            int tempX = ix+(-FILTER_OFFSET_X+mx);
                            if (tempX < 0) {
                                tempX = imageCopy[0].length + tempX;
                            } else if (tempX >= imageCopy[0].length) {
                                tempX %= imageCopy[0].length;
                            }
                            redVal += (imageCopy[tempY][tempX].getR() * masks[mz][my][mx]);
                            greenVal += (imageCopy[tempY][tempX].getG() * masks[mz][my][mx]);
                            blueVal += (imageCopy[tempY][tempX].getB() * masks[mz][my][mx]);
                        }
                    }
                    redValues.add(redVal * masksConstants[mz]);
                    greenValues.add(greenVal * masksConstants[mz]);
                    blueValues.add(blueVal * masksConstants[mz]);
                }

                int red = 0;
                int green = 0;
                int blue = 0;

                if (projection.equals("Magnitude")) {
                    red = rootOfSumOfSquares(redValues);
                    green = rootOfSumOfSquares(greenValues);
                    blue = rootOfSumOfSquares(blueValues);
                } else if (projection.equals("Direction")) {
                    red = inverseTan(redValues);
                    green = inverseTan(greenValues);
                    blue = inverseTan(blueValues);
                }

                if (colorMode.equals("Grayscale")) {
                    int tempValue = (int) Math.round((red + green + blue) / 3.0);
                    img[iy][ix].setRGB(tempValue, tempValue, tempValue);
                } else if (colorMode.equals("Color")) {
                    img[iy][ix].setRGB(red, green, blue);
                }

            }
        }

        return img;
    }

    public static CustomPixel[][] binaryImage(CustomPixel[][] image, int threshold) throws IllegalArgumentException {
        if (threshold < 0 || threshold > 255) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < image.length; i++) {
                for (int j = 0; j < image[0].length; j++) {
                    double bandAverage = (image[i][j].getR() + image[i][j].getG() + image[i][j].getB()) / 3.0;
                    if (bandAverage >= 0 && bandAverage < threshold) {
                        image[i][j].setRGB(0, 0, 0);
                    } else if (bandAverage >= threshold && bandAverage <= 255) {
                        image[i][j].setRGB(255, 255, 255);
                    }
                }
            }

            return image;
        }
    }

    public static CustomPixel[][] invertImage(CustomPixel[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                image[i][j].setRGB(255 - image[i][j].getR(), 255 - image[i][j].getG(), 255 - image[i][j].getB());
            }
        }

        return image;
    }

    public static CustomPixel[][] grayscaleImage(CustomPixel[][] image) {
        final double RED_WEIGHT = 0.2126;
        final double GREEN_WEIGHT = 0.7152;
        final double BLUE_WEIGHT = 0.0722;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                int tempValue = (int) Math.round((image[i][j].getR() * RED_WEIGHT) + (image[i][j].getG() * GREEN_WEIGHT)
                + (image[i][j].getB() * BLUE_WEIGHT));
                image[i][j].setRGB(tempValue, tempValue, tempValue);
            }
        }

        return image;
    }

    public static int calculateThreshold(CustomPixel[][] image) {
        int filter = calculateAveragePixelValue(image);
        double accumulator = 0;
        int counter = 0;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if (image[i][j].getR() > filter) {
                    accumulator += image[i][j].getR();
                    counter++;
                }
            }
        }
        //
        accumulator /= counter;
        accumulator = Math.round(accumulator);

        return (int) accumulator;
    }

    public static int calculateAveragePixelValue(CustomPixel[][] image) {
        double accumulator = 0;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                accumulator += image[i][j].getR();
            }
        }
        accumulator /= (image.length * image[0].length);
        accumulator = Math.round(accumulator);

        return (int) accumulator;
    }

    public static boolean isGrayscaleImage(CustomPixel[][] image) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                CustomPixel p = image[i][j];
                if (p.getR() != p.getG() || p.getR() != p.getB()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int countTraversablePixels(CustomPixel[][] image, int edgeThreshold) {
        int counter = 0;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if (image[i][j].getR() < edgeThreshold) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static int countNotTraversedTraversablePixels(CustomPixel[][] image, int edgeThreshold) {
        int counter = 0;

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                if (image[i][j].getR() < edgeThreshold && image[i][j].getTraversed() == 0) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static boolean isCorrectFileType(File file) {
        Pattern imageFilePattern = Pattern.compile(".+?\\.(png|jpe?g)$", Pattern.CASE_INSENSITIVE);

        if (imageFilePattern.matcher(file.getName()).matches()) {
            return true;
        }
        return false;
    }
}
