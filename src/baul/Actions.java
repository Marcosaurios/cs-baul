package baul;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFileChooser;
import java.io.File;

public class Actions extends Frame implements WindowListener,ActionListener {
        TextField text = new TextField(20);
        Button b;
        Button cF;
        JFileChooser fileChooser = new JFileChooser();
        
        private int numClicks = 0;

        public static void main(String[] args) {
                Actions myWindow = new Actions("Baul");
                myWindow.setSize(600,400);
                myWindow.setVisible(true);
                
                
                
        }

        public Actions(String title) {

                super(title);
                setLayout(new FlowLayout());
                addWindowListener(this);
                b = new Button("Ejecutar");
                cF = new Button("Elegir fichero");
                add(cF);
                add(b);
                add(text);
                b.addActionListener(this);
                
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                }
                
        }

        public void actionPerformed(ActionEvent e) {
                numClicks++;
                text.setText("Button Clicked " + numClicks + " times");
        }

        public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
        }

        public void windowOpened(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
        public void windowClosed(WindowEvent e) {}

}