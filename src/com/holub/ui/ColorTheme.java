package com.holub.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.holub.life.Universe;

public class ColorTheme {
    private static ColorTheme instance;
    public static Theme theme;

    ColorTheme() {
        createMenu();
        theme = new PurpleColorTheme();
    }

    ColorTheme(Theme theme) {
        createMenu();
        this.theme = theme;
    }

    public static synchronized ColorTheme getInstance() {
        if (instance == null) {
            synchronized (ColorTheme.class) {
                if (instance == null) {
                    instance = new ColorTheme(new PurpleColorTheme());  // default is purple color theme
                }
            }
        }
        return instance;
    }

    public void setColorTheme(Theme theme) {
        this.theme = theme;
    }

    private void createMenu() {

        ActionListener modifier =
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String name = ((JMenuItem) e.getSource()).getName();
                        if (name.equals("Purple")) {
                            ColorTheme.getInstance().setColorTheme(new PurpleColorTheme());
                        } else if (name.equals("Orange")) {
                            ColorTheme.getInstance().setColorTheme(new OrangeColorTheme());
                        } else if (name.equals("Blue")) {
                            ColorTheme.getInstance().setColorTheme(new BlueColorTheme());
                        }
                        Universe.instance().repaintNow();
                    }
                };
        MenuSite.addLine(this, "Theme", "Purple", modifier);
        MenuSite.addLine(this, "Theme", "Orange", modifier);
        MenuSite.addLine(this, "Theme", "Blue", modifier);
    }
}
