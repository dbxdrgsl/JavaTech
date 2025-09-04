package ap.lab06;

import java.awt.*;

public enum Player {
    BLUE(new Color(30,144,255)), RED(new Color(220,20,60));
    public final Color color;
    Player(Color c){ this.color=c; }
    public Player next(){ return this==BLUE? RED : BLUE; }
}
