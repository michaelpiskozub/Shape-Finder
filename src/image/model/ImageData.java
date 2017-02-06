package image.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;

public class ImageData extends Observable {
    private int imageWidth;
    private int imageHeight;
    private CustomPixel[][] image;
    private CustomPixel[][] imageWithRandomWalks;

    public void fetchImage(File imageFile) throws IOException {
        BufferedImage loadedBufferedImage = ImageIO.read(imageFile);
        imageWidth = loadedBufferedImage.getWidth();
        imageHeight = loadedBufferedImage.getHeight();
        image = new CustomPixel[imageHeight][imageWidth];
        for (int x=0; x<image[0].length; x++) {
            for (int y=0; y<image.length; y++) {
                int rgb = loadedBufferedImage.getRGB(x, y);
                image[y][x] = new CustomPixel(x, y, (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb & 0xFF), 0);
            }
        }
        imageWithRandomWalks = ImageOperations.copyImageArray(getImage());

        setChanged();
        notifyObservers();
    }

    public void saveImage(File destination, boolean saveImageWithRandomWalks) throws IllegalArgumentException, IOException {
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, 5);
        for (int x=0; x<image[0].length; x++) {
            for (int y=0; y<image.length; y++) {
                int rgb = 0;
                if (saveImageWithRandomWalks) {
                    rgb = imageWithRandomWalks[y][x].getR();
                    rgb = (rgb << 8) + imageWithRandomWalks[y][x].getG();
                    rgb = (rgb << 8) + imageWithRandomWalks[y][x].getB();
                } else {
                    rgb = image[y][x].getR();
                    rgb = (rgb << 8) + image[y][x].getG();
                    rgb = (rgb << 8) + image[y][x].getB();
                }

                bufferedImage.setRGB(x, y, rgb);
            }
        }
        String format = destination.getName().substring(destination.getName().lastIndexOf(".") + 1);
        ImageIO.write(bufferedImage, format, destination);
    }


    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public CustomPixel[][] getImage() {
        return image;
    }

    public CustomPixel getPixelFromImage(int x, int y) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            return ImageOperations.copyPixel(image[y][x]);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public CustomPixel[][] getImageWithRandomWalks() {
        return imageWithRandomWalks;
    }

    public int getTraversedInImagePixel(int x, int y) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            return image[y][x].getTraversed();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setImage(CustomPixel[][] newImage) {
        image = newImage;
        imageWithRandomWalks = ImageOperations.copyImageArray(getImage());

        setChanged();
        notifyObservers();
    }

    public void setRedInImageWithRandomWalksPixel(int x, int y, int redValue) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            imageWithRandomWalks[y][x].setR(redValue);
        }
    }

    public void setGreenInImageWithRandomWalksPixel(int x, int y, int greenValue) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            imageWithRandomWalks[y][x].setG(greenValue);
        }
    }

    public void setBlueInImageWithRandomWalksPixel(int x, int y, int blueValue) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            imageWithRandomWalks[y][x].setB(blueValue);
        }
    }

    public void setColorInImageWithRandomWalksPixel(int x, int y, Color color) {
        setRedInImageWithRandomWalksPixel(x, y, color.getRed());
        setGreenInImageWithRandomWalksPixel(x, y, color.getGreen());
        setBlueInImageWithRandomWalksPixel(x, y, color.getBlue());

        setChanged();
        notifyObservers();
    }

    public boolean isColorInImageWithRandomWalksPixel(int x, int y, Color color) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            CustomPixel p = imageWithRandomWalks[y][x];
            return (p.getR() == color.getRed()) && (p.getG() == color.getGreen()) && (p.getB() == color.getBlue());
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void incrementTraversedInImageWithRandomWalksPixel(int x, int y) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            imageWithRandomWalks[y][x].setTraversed(imageWithRandomWalks[y][x].getTraversed() + 1);
        }
    }

    public void incrementTraversedInImagePixel(int x, int y) {
        if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
            image[y][x].setTraversed(image[y][x].getTraversed() + 1);
        }
    }
}
