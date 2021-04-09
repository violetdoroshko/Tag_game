import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MyReader {
    Image readImage() throws IOException {
        Image img = ImageIO.read(new File("1.png"));
        return img;
    }
    Image readImageWithChooser() throws IOException {
        JFileChooser jfc = new JFileChooser("C:\\Users\\violv\\IdeaProjects\\EP_lab6\\6.1");
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            Image img = ImageIO.read(selectedFile);
            return img;
        }
        else return null;
    }
}
