package app;

import com.DisplayLocales;
import com.Info;
import com.SetLocale;

import java.util.*;

public class LocaleExplore {
    private static final String BUNDLE = "res.Messages";

    public static void main(String[] args) {
        Locale current = Locale.getDefault();
        ResourceBundle msg = ResourceBundle.getBundle(BUNDLE, current);

        var disp = new DisplayLocales(msg);
        var set  = new SetLocale();
        var info = new Info();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(msg.getString("prompt") + " ");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            String[] t = line.split("\\s+");
            String cmd = t[0].toLowerCase(Locale.ROOT);

            switch (cmd) {
                case "exit" -> { return; }
                case "locales" -> disp.run();
                case "set" -> {
                    if (t.length < 2) { System.out.println("usage: set <lang[-COUNTRY]>"); break; }
                    try {
                        current = set.set(t[1]);
                        Locale.setDefault(current);
                        msg = ResourceBundle.getBundle(BUNDLE, current);
                        set.printConfirm(msg, current);
                    } catch (Exception e){ System.out.println("bad locale tag"); }
                }
                case "info" -> {
                    Locale target = (t.length>=2)? Locale.forLanguageTag(t[1]) : current;
                    info.show(msg, target);
                }
                default -> System.out.println(msg.getString("invalid"));
            }
        }
    }
}
