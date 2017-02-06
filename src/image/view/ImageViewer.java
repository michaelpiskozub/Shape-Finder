package image.view;

import image.model.CustomPixel;
import image.model.ImageData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class ImageViewer extends JFrame implements Observer {
    private ImageData imgData;
    private boolean showRandomWalks;
    private JPanel imagePanel;
    private Point anchorPoint;
    private JScrollPane jsp;
    private int zoomRate;
    private CustomPixel[][] image;
    private CustomPixel[][] imageWithRandomWalks;

    public ImageViewer(ImageData imgData) {
        super("Image Viewer [x1]");

        this.imgData = imgData;
        this.showRandomWalks = false;

        this.zoomRate = 1;

        initImagePanel();

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1100, 750);
    }

    public void setImage(CustomPixel[][] image) {
        this.image = image;
    }

    public void setShowRandomWalks(boolean showRandomWalks) {
        this.showRandomWalks = showRandomWalks;
        imagePanel.repaint();
    }

    private void setZoomRate(int rate) {
        this.zoomRate = Math.max(this.zoomRate + rate, 1);
    }

    private void repaintZoomedImage(int zoom) {
        setZoomRate(zoom);
        setTitle("Image Viewer [x" + this.zoomRate + "]");
        imagePanel.repaint();
        imagePanel.setPreferredSize(new Dimension(
                imgData.getImageWidth() * zoomRate,
                imgData.getImageHeight() * zoomRate));
    }

    public void initImagePanel() {
        imagePanel = new JPanel() {
            {
                setOpaque(false);
            }

            @Override
            public void paint(Graphics g) {
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.WHITE);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);

                if (image != null) {
                    for (int x = 0; x < image[0].length; x++) {
                        for (int y = 0; y < image.length; y++) {
                            if (showRandomWalks && imageWithRandomWalks != null) {
                                g.setColor(new Color(imageWithRandomWalks[y][x].getR(), imageWithRandomWalks[y][x].getG(), imageWithRandomWalks[y][x].getB()));
                            } else {
                                g.setColor(new Color(image[y][x].getR(), image[y][x].getG(), image[y][x].getB()));
                            }
                            g.fillRect(x*zoomRate, y*zoomRate, zoomRate, zoomRate);
                        }
                    }
                }
            }
        };

        imagePanel.setPreferredSize(new Dimension(
                imgData.getImageWidth() * zoomRate,
                imgData.getImageHeight() * zoomRate
        ));
        jsp = new JScrollPane(imagePanel);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(jsp);

        imagePanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                int modifiers = e.getModifiers();
                if ((modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    Point p = e.getPoint();
                    int px = (int) p.getX();
                    int py = (int) p.getY();
                    if (px >= 0 && px < (imgData.getImageWidth() * zoomRate) && py >= 0 && py < (imgData.getImageHeight() * zoomRate)) {
                        int x = (int) (Math.floor(px / zoomRate));
                        int y = (int) (Math.floor(py / zoomRate));
                        CustomPixel pix = imgData.getPixelFromImage(x, y);
                        JOptionPane.showMessageDialog(null, pix, "Pixel Information", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println(pix);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

        });

        imagePanel.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e) {
                anchorPoint = e.getPoint();
                imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                Point mouse = me.getPoint();
                Point panel = imagePanel.getLocation();
                Point position = new Point(panel.x+(mouse.x-anchorPoint.x), panel.y+(mouse.y-anchorPoint.y));
                imagePanel.setLocation(position);
                jsp.revalidate();
            }
        });

        imagePanel.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int steps = e.getWheelRotation();
                repaintZoomedImage(-steps);
            }
        });

        addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP) {
                    repaintZoomedImage(1);
                } else if (key == KeyEvent.VK_DOWN) {
                    repaintZoomedImage(-1);
                } else if (key == KeyEvent.VK_LEFT) {

                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == imgData) {
            image = imgData.getImage();
            imageWithRandomWalks = imgData.getImageWithRandomWalks();
            imagePanel.repaint();
        }
    }
}
