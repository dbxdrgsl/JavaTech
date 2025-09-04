package com;

import java.util.Locale;
import java.util.ResourceBundle;

public class DisplayLocales implements Runnable {
    private final ResourceBundle msg;
    public DisplayLocales(ResourceBundle msg){ this.msg = msg; }

    @Override public void run() {
        System.out.println(msg.getString("locales"));
        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getDisplayName().isBlank()) continue;
            System.out.printf("%s\t(%s)%n", l.toLanguageTag(), l.getDisplayName(l));
        }
    }
}
