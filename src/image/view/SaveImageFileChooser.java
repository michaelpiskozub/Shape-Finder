package image.view;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.regex.Pattern;

public class SaveImageFileChooser extends JFileChooser {
    private Pattern imageFilePattern;

    public SaveImageFileChooser(File currentDirectory) {
        super(currentDirectory);

        initComponents();
    }

    private void initComponents() {
        imageFilePattern = Pattern.compile(".+?\\.(png|jpe?g)$", Pattern.CASE_INSENSITIVE);

        this.setDialogTitle("Save Image...");
        this.setAcceptAllFileFilterUsed(false);
        this.setFileFilter(new ImageFilter());
    }

    private class ImageFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return imageFilePattern.matcher(f.getName()).matches();
        }

        @Override
        public String getDescription() {
            return "JPG or JPEG or PNG";
        }

    }
}
