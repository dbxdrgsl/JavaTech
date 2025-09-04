package ap.lab06;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public final class MainFrame extends JFrame {
    public final GameModel model = new GameModel();
    public final ConfigPanel config = new ConfigPanel(this);
    public final DrawingPanel canvas = new DrawingPanel(this);
    public final ControlPanel control = new ControlPanel(this);

    public MainFrame(){
        super("Dots Game — Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(config, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(control, BorderLayout.SOUTH);
        setSize(900, 650);
        setLocationRelativeTo(null);
    }

    public void newGame(int nDots){
        model.clear();
        int w = canvas.getWidth(), h = canvas.getHeight();
        if (w<=0) { w=800; h=500; } // first time
        var rnd = new java.util.Random();
        int margin=40;
        for(int i=0;i<nDots;i++){
            double x = margin + rnd.nextDouble()*(w-2.0*margin);
            double y = margin + rnd.nextDouble()*(h-2.0*margin);
            model.dots.add(new Dot(x,y));
        }
        canvas.repaint();
    }

    public void saveModel(File f) throws IOException {
        try (var out = new ObjectOutputStream(new FileOutputStream(f))) { out.writeObject(model); }
    }

    public void loadModel(File f) throws IOException, ClassNotFoundException {
        try (var in = new ObjectInputStream(new FileInputStream(f))) {
            GameModel m = (GameModel) in.readObject();
            model.dots.clear(); model.dots.addAll(m.dots);
            model.edges.clear(); model.edges.addAll(m.edges);
            model.turn = m.turn;
            canvas.repaint();
        }
    }
}
