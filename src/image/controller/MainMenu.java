package image.controller;

import image.model.*;
import image.view.ImageViewer;
import image.view.LoadImageFileChooser;
import image.view.SaveImageFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainMenu extends JFrame implements ActionListener, ItemListener {
    private Menu menu;
    private ImageViewer imgViewer;
    private ImageData imgData;
    private ArrayList<Traversal> traversalsList;
    private boolean isTraversing;
    private JFileChooser jfcLoadImage;
    private JFileChooser jfcSaveImage;
    private File imageToDisplay;
    private int edgeThreshold;
    private final int GAP = 5;
    private Inspector inspector;
    private Thread viewerListener;
    private Thread traversalLabelUpdater;
    private final String CURRENT_DIRECTORY = ".";

    public MainMenu() {
        super("Menu");
        menu = new Menu();

        setContentPane(menu.mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 440);
        setResizable(false);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        traversalsList = new ArrayList<Traversal>();
        imgData = new ImageData();
        imgViewer = new ImageViewer(imgData);
        imgViewer.setLocation((int) this.getSize().getWidth() + GAP, 0);
        imgData.addObserver(imgViewer);


        jfcLoadImage = new LoadImageFileChooser(new File(CURRENT_DIRECTORY));
        jfcSaveImage = new SaveImageFileChooser(new File(CURRENT_DIRECTORY));


        menu.jftfBinaryThreshold.setText("128");

        ButtonGroup bgRadioButtons = new ButtonGroup();
        bgRadioButtons.add(menu.jrbOriginal);
        bgRadioButtons.add(menu.jrbGrayscale);
        bgRadioButtons.add(menu.jrbBinary);
        bgRadioButtons.add(menu.jrbInvert);
        bgRadioButtons.add(menu.jrbRobertsCross);
        bgRadioButtons.add(menu.jrbSobel);
        bgRadioButtons.add(menu.jrbPrewitt);
        bgRadioButtons.add(menu.jrbScharr);

        enableComponents(menu.jpTools, false);
        enableComponents(menu.jpOperators, false);
        enableComponents(menu.jpTraversal, false);

        menu.jrbOriginal.addActionListener(this);
        menu.jrbGrayscale.addActionListener(this);
        menu.jrbBinary.addActionListener(this);
        menu.jrbInvert.addActionListener(this);
        menu.jrbRobertsCross.addActionListener(this);
        menu.jrbSobel.addActionListener(this);
        menu.jrbPrewitt.addActionListener(this);
        menu.jrbScharr.addActionListener(this);
        menu.jbAddTraversalThread.addActionListener(this);
        menu.jbLoadImage.addActionListener(this);
        menu.jbSaveImage.addActionListener(this);

        menu.jcbEnableTraversal.addItemListener(this);
        menu.jcbShowRandomWalks.addItemListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menu.jrbOriginal) {
            stopTraversal();
            try {
                imgData.fetchImage(imageToDisplay);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == menu.jrbGrayscale) {
            applyOperation("Grayscale");
        } else if (e.getSource() == menu.jrbBinary) {
            applyOperation("Binary");
        } else if (e.getSource() == menu.jrbInvert) {
            applyOperation("Invert");
        } else if (e.getSource() == menu.jrbRobertsCross) {
            applyOperation("Roberts Cross");
        } else if (e.getSource() == menu.jrbSobel) {
            applyOperation("Sobel");
        } else if (e.getSource() == menu.jrbPrewitt) {
            applyOperation("Prewitt");
        } else if (e.getSource() == menu.jrbScharr) {
            applyOperation("Scharr");
        } else if (e.getSource() == menu.jbAddTraversalThread) {
            String traversalsLocation = menu.jcbTraversalsLocation.getSelectedItem() + "";
            int traversalsAmount = Integer.parseInt(menu.jcbTraversalsAmount.getSelectedItem() + "");
            int imageWidth = imgData.getImageWidth();
            int imageHeight = imgData.getImageHeight();
            if (traversalsLocation.equals("Regular")) {
                int gridX = (int) Math.floor(imageWidth / (traversalsAmount + 1));
                int gridY = (int) Math.floor(imageHeight / (traversalsAmount + 1));
                for (int i = gridY; i <= traversalsAmount * gridY; i += gridY) {
                    for (int j = gridX; j <= traversalsAmount * gridX; j += gridX) {
                        addTraversalThread(imgData.getPixelFromImage(j, i));
                    }
                }
            } else if (traversalsLocation.equals("Random")) {
                for (int i = 0; i < traversalsAmount; i++) {
                    CustomPixel p;
                    do {
                        p = imgData.getPixelFromImage(
                                new Random().nextInt(imageWidth),
                                new Random().nextInt(imageHeight));
                    } while (p.getR() >= edgeThreshold && p.getTraversed() == 0 &&
                            imgData.getPixelFromImage(p.getX() + 1, p.getY()).getR() >= edgeThreshold &&
                            imgData.getPixelFromImage(p.getX() - 1, p.getY()).getR() >= edgeThreshold &&
                            imgData.getPixelFromImage(p.getX(), p.getY() + 1).getR() >= edgeThreshold &&
                            imgData.getPixelFromImage(p.getX(), p.getY() - 1).getR() >= edgeThreshold);
                    addTraversalThread(p);
                }
            }
        } else if (e.getSource() == menu.jbLoadImage) {
            int returnVal = jfcLoadImage.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                stopTraversal();
                imageToDisplay = jfcLoadImage.getSelectedFile();
                try {
                    imgData.fetchImage(imageToDisplay);
                    imgViewer.setVisible(true);

                    if (viewerListener == null || !viewerListener.isAlive()) {
                        addViewerListener();
                    }

                    if (traversalLabelUpdater == null || !traversalLabelUpdater.isAlive()) {
                        addTraversalLabelUpdater();
                    }

                    Colors.addRandomColors();

                    menu.jrbOriginal.setSelected(true);
                    menu.jcbEnableTraversal.setSelected(false);

                    menu.jcbEnableTraversal.setEnabled(true);
                    enableComponents(menu.jpTools, true);
                    enableComponents(menu.jpOperators, true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource() == menu.jbSaveImage) {
            int returnVal = jfcSaveImage.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (ImageOperations.isCorrectFileType(jfcSaveImage.getSelectedFile())) {
                    try {
                        imgData.saveImage(jfcSaveImage.getSelectedFile(), menu.jcbShowRandomWalks.isSelected());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Something went wrong.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect file type.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getItemSelectable() == menu.jcbEnableTraversal) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (ImageOperations.isGrayscaleImage(imgData.getImage())) {
                    pauseTraversal(false);

                    enableComponents(menu.jpTools, false);
                    enableComponents(menu.jpOperators, false);
                    enableComponents(menu.jpTraversal, true);
                    menu.jcbShowRandomWalks.setSelected(true);

                    edgeThreshold = ImageOperations.calculateThreshold(imgData.getImage());
                    inspector = new Inspector(traversalsList, imgData, edgeThreshold);
                } else {
                    menu.jcbEnableTraversal.setSelected(false);
                    JOptionPane.showMessageDialog(this, "Image has to be grayscale.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                pauseTraversal(true);

                enableComponents(menu.jpTools, true);
                enableComponents(menu.jpOperators, true);
                enableComponents(menu.jpTraversal, false);
                menu.jcbEnableTraversal.setEnabled(true);
            }
        } else if (e.getItemSelectable() == menu.jcbShowRandomWalks) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                imgViewer.setShowRandomWalks(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                imgViewer.setShowRandomWalks(false);
            }
        }
    }

    private void pauseTraversal(boolean b) {
        if (isTraversing) {
            for (Traversal t : traversalsList) {
                t.setPauseTraversal(b);
            }
        }
    }

    private void stopTraversal() {
        if (isTraversing) {
            for (Traversal t : traversalsList) {
                t.setStopTraversal(true);
            }
            traversalsList.clear();
            isTraversing = false;
        }
    }

    private void addTraversalThread(CustomPixel p) {
        if (traversalsList.size() < Colors.NUMBER_OF_COLORS) {
            traversalsList.add(new Traversal(imgData, p, edgeThreshold, traversalsList.size() + 1 + "", inspector));
            if (!isTraversing) {
                isTraversing = true;
            }
        }
    }

    private void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
        }
    }

    private void applyOperation(String operationName) {
        stopTraversal();
        if (!menu.jcbCompoundMode.isSelected()) {
            try {
                imgData.fetchImage(imageToDisplay);
            } catch (IOException ex) {ex.printStackTrace();}
        }
        if (operationName.equals("Grayscale")) {
            imgData.setImage(ImageOperations.grayscaleImage(imgData.getImage()));
        } else if (operationName.equals("Binary")) {
            try {
                imgData.setImage(ImageOperations.binaryImage(imgData.getImage(),
                        Integer.parseInt(menu.jftfBinaryThreshold.getText())));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Wrong threshold value.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else if (operationName.equals("Invert")) {
            imgData.setImage(ImageOperations.invertImage(imgData.getImage()));
        } else if (operationName.equals("Roberts Cross")) {
            imgData.setImage(ImageOperations.robertsCrossOperator(
                    imgData.getImage(),
                    menu.jcbColorMode.getSelectedItem() + "",
                    menu.jcbProjectionType.getSelectedItem() + ""));
        } else if (operationName.equals("Sobel")) {
            imgData.setImage(ImageOperations.sobelOperator(
                    imgData.getImage(),
                    menu.jcbColorMode.getSelectedItem() + "",
                    menu.jcbProjectionType.getSelectedItem() + ""));
        } else if (operationName.equals("Prewitt")) {
            imgData.setImage(ImageOperations.prewittOperator(
                    imgData.getImage(),
                    menu.jcbColorMode.getSelectedItem() + "",
                    menu.jcbProjectionType.getSelectedItem() + ""));
        } else if (operationName.equals("Scharr")) {
            imgData.setImage(ImageOperations.scharrOperator(
                    imgData.getImage(),
                    menu.jcbColorMode.getSelectedItem() + "",
                    menu.jcbProjectionType.getSelectedItem() + ""));
        } else {
            JOptionPane.showMessageDialog(this, "Wrong operation name specified.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addViewerListener() {
        viewerListener = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!imgViewer.isShowing()) {
                        enableComponents(menu.jpTools, false);
                        enableComponents(menu.jpOperators, false);
                        enableComponents(menu.jpTraversal, false);
                        return;
                    }
                    try {sleep(200);} catch (InterruptedException e1) {}
                }
            }
        };
        viewerListener.start();
    }

    private void addTraversalLabelUpdater() {
        traversalLabelUpdater = new Thread() {
            @Override
            public void run() {
                while (true) {
                    int counter = 0;
                    for (Traversal t : traversalsList) {
                        if (t.getThread().isAlive()) {
                            counter++;
                        }
                    }

                    String activeThreadsText = "Active Threads: " + counter;
                    if (!menu.jlActiveThreads.getText().equals(activeThreadsText)) {
                        menu.jlActiveThreads.setText(activeThreadsText);
                    }

                    String allThreadsText = "Traversal Threads: " + traversalsList.size();
                    if (!menu.jlTraversalThreads.getText().equals(allThreadsText)) {
                        menu.jlTraversalThreads.setText(allThreadsText);
                    }

                    try {sleep(500);} catch (InterruptedException e1) {}
                }
            }
        };
        traversalLabelUpdater.start();
    }
}
