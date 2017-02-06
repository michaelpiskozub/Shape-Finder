package image.controller;

import javax.swing.*;

public class Menu {

    protected JPanel mainPanel;
    protected JRadioButton jrbBinary;
    protected JRadioButton jrbOriginal;
    protected JRadioButton jrbInvert;
    protected JButton jbCalculateThreshold;
    protected JButton jbLoadImage;
    protected JButton jbSaveImage;
    protected JComboBox jcbColorMode;
    protected JRadioButton jrbRobertsCross;
    protected JRadioButton jrbSobel;
    protected JRadioButton jrbScharr;
    protected JRadioButton jrbPrewitt;
    protected JCheckBox jcbEnableTraversal;
    protected JButton jbAddTraversalThread;
    protected JTabbedPane jtpMain;
    protected JPanel jpTools;
    protected JPanel jpOperators;
    protected JPanel jpTraversal;
    protected JLabel jlTraversalThreads;
    protected JRadioButton jrbGrayscale;
    protected JCheckBox jcbCompoundMode;
    protected JButton jbCalculateAverage;
    protected JButton jbCalculateRatio;
    protected JComboBox jcbProjectionType;
    protected JPanel jpColorMode;
    protected JPanel jpProjectionType;
    protected JFormattedTextField jftfBinaryThreshold;
    protected JCheckBox jcbShowRandomWalks;
    protected JComboBox jcbTraversalsLocation;
    protected JComboBox jcbTraversalsAmount;
    protected JPanel jpTraversalsAmount;
    protected JPanel jpTraversalsLocation;
    protected JPanel jpNewTraversals;
    protected JLabel jlActiveThreads;
}
