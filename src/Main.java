import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Main extends JFrame {

    private JPanel panelWithPeaces;
    private JPanel panelWithImage;
    private JPanel panelWithButtons;
    private JPanel panelWithImages;
    final static int NUMBER = 2;            //колво полей
    private ImageIcon image;
    private int[] myField;
    private ImageIcon[] icons;
    private int heightOfPeace, widthOfPeace;
    private int heightOfImage, widthOfImage;
    private BufferedImage bufferedImage;
    private JButton openImage = new JButton("open");
    private JButton newPuzzle = new JButton("new");

    public Main() {
        super("Lab 6");
        setLocation(30, 100);
        setLayout(new BorderLayout());
        panelWithImages =  new JPanel(new GridLayout(1, 2, 0, 0));
        panelWithButtons =  new JPanel();
        panelWithPeaces = new JPanel(new GridLayout(NUMBER, NUMBER, 0, 0));
        panelWithImage = new JPanel(new GridLayout(1, 0, 0, 0));

        myField = new int[NUMBER * NUMBER];

        try {
            MyReader reader = new MyReader();
            image = new ImageIcon(reader.readImage());

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Please, open right file!");
        }

        heightOfImage = image.getIconHeight();
        widthOfImage = image.getIconWidth();
        heightOfPeace = heightOfImage / NUMBER;
        widthOfPeace = widthOfImage / NUMBER;

        add(panelWithButtons, BorderLayout.NORTH);
        add(panelWithImages, BorderLayout.SOUTH);
        panelWithImages.add(panelWithPeaces);
        panelWithImages.add(panelWithImage);
        panelWithButtons.setPreferredSize(new Dimension(getWidth(), 55));
        panelWithButtons.add(openImage);
        panelWithButtons.add(newPuzzle);
        panelWithImage.add(new JLabel(image));
        setVisible(true);
        bufferedImage = toBufferedImage(image.getImage());

        //открыть новую картинку
        openImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        //генерация новой последовательности
        newPuzzle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewGame();
                repaintPanel();
            }
        });


        initializePeacesOfImage();
        generateNewGame();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void openFile() {
        try {
            MyReader reader = new MyReader();
            Image img = reader.readImageWithChooser();
            image = new ImageIcon(img);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "File problems!");
        }

        heightOfImage = image.getIconHeight();
        widthOfImage = image.getIconWidth();
        heightOfPeace = heightOfImage / NUMBER;
        widthOfPeace = widthOfImage / NUMBER;

        panelWithPeaces.setPreferredSize(new Dimension(widthOfImage + 10, heightOfImage + 10));
        panelWithImage.setPreferredSize(new Dimension(widthOfImage + 10, heightOfImage + 10));
        panelWithImages.setPreferredSize(new Dimension(widthOfImage * 2 + 20, heightOfImage + 10));
        panelWithImages.setBounds(0, 0, widthOfImage * 2 + 20, heightOfImage + 10);

        panelWithImage.removeAll();
        panelWithImage.add(new JLabel(image));

        initializePeacesOfImage();
        generateNewGame();

    }

    public void generateNewGame() {
        Random rand = new Random();
        do {
            for (int i = 0; i < NUMBER * NUMBER; i++) {
                myField[i] = (i + 1) % (NUMBER * NUMBER);
            }
            int n = NUMBER * NUMBER -1;

            while (n > 1) {
                int r = rand.nextInt(n--);
                int tmp = myField[r];
                myField[r] = myField[n];
                myField[n] = tmp;
            }
        } while (!isSolvable() || isSolved());

        repaintPanel();
    }

    //правило игры в пятнашки!!!
    //количество инверсий только четное
    private boolean isSolvable() {
        int countInversions = 0;

        for (int i = 0; i < NUMBER * NUMBER -1; i++) {
            for (int j = 0; j < i; j++) {
                if (myField[j] > myField[i])
                    countInversions++;
            }
        }

        return countInversions % 2 == 0;
    }

    public void repaintPanel() {
        panelWithPeaces.removeAll();
        int index = 0;
        for (int i = 0; i < NUMBER * NUMBER; i++) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(widthOfPeace, heightOfPeace));
            button.setFocusable(false);
            index = myField[i];
            //подстраиваем индексацию(так как у нас 0 - была пустая клетка, а в icons 0-это первая часть)
            if (index == 0)
                index = NUMBER * NUMBER - 1;
            else index--;
            button.setIcon(icons[index]);
            //устанавливает число, но мы его не видим из-за картинки
            button.setText(Integer.toString(myField[i]));
            panelWithPeaces.add(button);

            if (myField[i] == 0) {
                button.setIcon(null);
            } else
                button.addActionListener(new MyActionListener());
        }

    }

    public void initializePeacesOfImage() {

        icons = new ImageIcon[NUMBER * NUMBER];
        BufferedImage bufferedImage = toBufferedImage(image.getImage());
        BufferedImage peace;
        for (int i = 0; i < NUMBER; i++) {
            for (int j = 0; j < NUMBER; j++) {
                //x, y - верхний левый угол
                //смещается на длину и ширину кусочка
                peace = bufferedImage.getSubimage(j * widthOfPeace, i * heightOfPeace, widthOfPeace, heightOfPeace);
                icons[i * NUMBER + j] = new ImageIcon(peace);
            }
        }
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = image.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return image;
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int row = 0;
            int column = 0;
            int num = Integer.parseInt(button.getText());
            int k = 0;
            //смотрим в какой строке и столбце мы находимся
            for (int i = 0; i < NUMBER; i++) {
                for (int j = 0; j < NUMBER; j++) {
                    if (myField[k] == num) {
                        row = i;
                        column = j;
                        k++;
                        break;
                    }
                    k++;
                }
            }
            if (column > 0) {//можем двигаться влево
                if (myField[row * NUMBER + column - 1] == 0) {
                    myField[row * NUMBER + column - 1] = num;
                    myField[row * NUMBER + column] = 0;
                }
            }
            if (column < NUMBER - 1) {//можем двигаться вправо
                if (myField[row * NUMBER + column + 1] == 0) {
                    myField[row * NUMBER + column + 1] = num;
                    myField[row * NUMBER + column] = 0;
                }
            }
            if (row > 0) {//можем двигаться вверх
                if (myField[(row - 1) * NUMBER + column] == 0) {
                    myField[(row - 1) * NUMBER + column] = num;
                    myField[(row) * NUMBER + column] = 0;
                }
            }
            if (row < NUMBER - 1) {//можем двигаться вниз
                if (myField[(row + 1) * NUMBER + column] == 0) {
                    myField[(row + 1) * NUMBER + column] = num;
                    myField[(row) * NUMBER + column] = 0;
                }
            }

            repaintPanel();
            if (isSolved()) {
                JOptionPane.showMessageDialog(null,
                        "GOOD JOB!");
            }
        }
    }

    private boolean isSolved() {
        if (myField[NUMBER * NUMBER - 1] != 0) //клетка в правом нижнем углу должна быть пустой
            return false;

        for (int i = 0; i < NUMBER * NUMBER - 1; i++) {
            if (myField[i] != i + 1)
                return false;
        }

        return true;
    }

    public static void main(String[] args) {
        new Main();
    }

}

