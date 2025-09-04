package ap.lab06;

import javax.swing.*;
import java.awt.*;

public final class ConfigPanel extends JPanel {
    private final JSpinner dotCount = new JSpinner(new SpinnerNumberModel(12, 2, 200, 1));
    public ConfigPanel(MainFrame frame){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Dots:"));
        add(dotCount);
        var btn = new JButton("New Game");
        btn.addActionListener(e -> frame.newGame((int)dotCount.getValue()));
        add(btn);
        add(new JLabel("   Turn:"));
        var turnLbl = new JLabel(frame.model.turn.name());
        add(turnLbl);
        // auto-update on repaint
        Timer t = new Timer(200, e -> turnLbl.setText(frame.model.turn.name()));
        t.start();
    }
}
