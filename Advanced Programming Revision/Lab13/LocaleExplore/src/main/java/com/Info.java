package com;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.*;

public class Info {
    public void show(ResourceBundle msg, Locale target){
        System.out.println(msg.getString("info").replace("{0}", target.toLanguageTag()));

        // Country / Language in both target and default (English)
        Locale en = Locale.ENGLISH;
        String country = Optional.ofNullable(target.getCountry()).filter(s->!s.isEmpty()).map(s->new Locale("", s)).map(l->l.getDisplayCountry(target)).orElse("-");
        String countryEN = Optional.ofNullable(target.getCountry()).filter(s->!s.isEmpty()).map(s->new Locale("", s)).map(l->l.getDisplayCountry(en)).orElse("-");
        String lang = target.getDisplayLanguage(target);
        String langEN = target.getDisplayLanguage(en);

        // Currency
        String curCode = "-"; String curDisp = "-";
        try { Currency c = Currency.getInstance(target); curCode = c.getCurrencyCode(); curDisp = c.getDisplayName(target); } catch (Exception ignored) {}

        // Weekdays and months
        DateFormatSymbols sym = DateFormatSymbols.getInstance(target);
        String[] w = sym.getWeekdays(); // index 1..7
        String[] m = sym.getMonths();   // 0..11
        String weekdays = String.join(", ", Arrays.asList(w).subList(2,8)) + ", " + w[1]; // start Monday
        String months = String.join(", ", Arrays.asList(m).subList(0,12));

        // Today formatted
        String todayEN = DateFormat.getDateInstance(DateFormat.LONG, en).format(new Date());
        String todayLO = DateFormat.getDateInstance(DateFormat.LONG, target).format(new Date());

        System.out.printf("Country: %s (%s)%n", countryEN, country);
        System.out.printf("Language: %s (%s)%n", langEN, lang);
        System.out.printf("Currency: %s (%s)%n", curCode, curDisp);
        System.out.println("Week Days: " + weekdays);
        System.out.println("Months: " + months);
        System.out.printf("Today: %s (%s)%n", todayEN, todayLO);
    }
}
