package image.model;

public class CustomPixel {
    private int x;
    private int y;
    private int r;
    private int g;
    private int b;
    private int traversed;

    public CustomPixel(int x, int y, int r, int g, int b, int traversed) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.traversed = traversed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public int getTraversed() {
        return traversed;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setB(int b) {
        this.b = b;
    }

    public void setRGB(int r, int g, int b) {
        setR(r);
        setG(g);
        setB(b);
    }

    public void setTraversed(int traversed) {
        this.traversed = traversed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomPixel that = (CustomPixel) o;

        if (x != that.x) return false;
        if (y != that.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "(x = " + x + ", y = " + y + ", r = " + r + ", g = " + g + ", b = " + b + ")";
    }
}
