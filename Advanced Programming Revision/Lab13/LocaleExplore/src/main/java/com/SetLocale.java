package com;

import java.util.Locale;
import java.util.ResourceBundle;

public class SetLocale {
    public Locale set(String tag){
        Locale loc = Locale.forLanguageTag(tag);
        if (loc == null || loc.toLanguageTag().isEmpty()) throw new IllegalArgumentException("bad tag");
        return loc;
    }
    public void printConfirm(ResourceBundle msg, Locale loc){
        System.out.println(msg.getString("locale.set").replace("{0}", loc.toLanguageTag()));
    }
}
