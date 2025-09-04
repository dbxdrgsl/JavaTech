package ap.lab06;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class DrawingPanel extends JPanel {
    private final MainFrame frame;
    private Integer sel = null;          // index of start dot
    private Point mouse = null;          // current mouse for rubber band
    private static final int R = 6;      // dot radius

    public DrawingPanel(MainFrame frame){
        this.frame = frame;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 500));

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                sel = hit(e.getX(), e.getY());
                mouse = e.getPoint();
                repaint();
            }
            @Override public void mouseDragged(MouseEvent e) {
                mouse = e.getPoint();
                repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                int b = hit(e.getX(), e.getY());
                if (sel != null && b >= 0 && b != sel) frame.model.addEdge(sel, b);
                sel = null; mouse = null; repaint();
                if (frame.model.isConnected()) Toolkit.getDefaultToolkit().beep();
            }
            @Override public void mouseMoved(MouseEvent e) { mouse = e.getPoint(); repaint(); }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private int hit(int x, int y){
        var dots = frame.model.dots;
        for (int i=0;i<dots.size();i++){
            double dx = x - dots.get(i).x(), dy = y - dots.get(i).y();
            if (dx*dx + dy*dy <= (R+3)*(R+3)) return i;
        }
        return -1;
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // edges
        for (Edge e : frame.model.edges) {
            var a = frame.model.dots.get(e.u);
            var b = frame.model.dots.get(e.v);
            g2.setStroke(new BasicStroke(2.2f));
            g2.setColor(e.owner.color);
            g2.drawLine((int)a.x(), (int)a.y(), (int)b.x(), (int)b.y());
        }

        // rubber band
        if (sel != null && mouse != null) {
            var a = frame.model.dots.get(sel);
            g2.setColor(new Color(0,0,0,120));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{6f,6f}, 0));
            g2.drawLine((int)a.x(), (int)a.y(), mouse.x, mouse.y);
        }

        // dots
        for (int i=0;i<frame.model.dots.size();i++){
            var d = frame.model.dots.get(i);
            int x=(int)d.x(), y=(int)d.y();
            g2.setColor(Color.BLACK);
            g2.fillOval(x-R, y-R, 2*R, 2*R);
            g2.setColor(Color.WHITE);
            g2.fillOval(x-R+2, y-R+2, 2*R-4, 2*R-4);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf(i+1), x+7, y-7);
        }

        // footer info
        g2.setColor(new Color(0,0,0,150));
        String info = "Turn: " + frame.model.turn + " | edges=" + frame.model.edges.size()
                + " | len=" + String.format("%.2f", frame.model.totalLength());
        g2.drawString(info, 10, getHeight()-10);
        g2.dispose();
    }
}
