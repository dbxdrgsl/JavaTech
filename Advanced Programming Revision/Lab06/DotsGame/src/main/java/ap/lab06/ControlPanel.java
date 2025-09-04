package ap.lab06;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class ControlPanel extends JPanel {
    public ControlPanel(MainFrame frame){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton load = new JButton("Load"), save = new JButton("Save"),
                export = new JButton("Export PNG"), exit = new JButton("Exit"),
                stats = new JButton("Score");

        load.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION) {
                try { frame.loadModel(fc.getSelectedFile()); }
                catch (Exception ex){ JOptionPane.showMessageDialog(frame, ex.getMessage(), "Load error", JOptionPane.ERROR_MESSAGE); }
            }
        });

        save.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION) {
                try { frame.saveModel(fc.getSelectedFile()); }
                catch (Exception ex){ JOptionPane.showMessageDialog(frame, ex.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE); }
            }
        });

        export.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    BufferedImage img = new BufferedImage(frame.canvas.getWidth(), frame.canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.canvas.printAll(g2);
                    g2.dispose();
                    ImageIO.write(img, "png", f);
                } catch (Exception ex){ JOptionPane.showMessageDialog(frame, ex.getMessage(), "Export error", JOptionPane.ERROR_MESSAGE); }
            }
        });

        stats.addActionListener(e -> {
            double total = frame.model.totalLength();
            double mst   = frame.model.mstLength();
            boolean connected = frame.model.isConnected();
            JOptionPane.showMessageDialog(frame,
                    "Connected: " + connected + "\n" +
                            "Current length: " + String.format("%.2f", total) + "\n" +
                            "MST length: " + String.format("%.2f", mst) + "\n" +
                            "Overhead: " + String.format("%.2f", (total - mst)));
        });

        exit.addActionListener(e -> System.exit(0));

        add(load); add(save); add(export); add(stats); add(exit);
    }
}
