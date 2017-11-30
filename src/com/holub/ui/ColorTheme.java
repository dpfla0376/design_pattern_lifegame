package com.holub.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.holub.life.Universe;

public class ColorTheme {
    private static ColorTheme instance;
    public static Theme theme;

    public static Color BORDER_COLOR;
    public static Color LIVE_COLOR;
    public static Color DEAD_COLOR;
    public static Color NEIGHBOR_BORDER_COLOR;

    ColorTheme() {
        createMenu();
        setColorTheme(new OrangeColorTheme());
    }

    ColorTheme(Theme theme) {
        createMenu();
        setColorTheme(theme);

    }

    public static synchronized ColorTheme getInstance() {
        if (instance == null) {
            synchronized (ColorTheme.class) {
                if (instance == null) {
                    instance = new ColorTheme(new OrangeColorTheme());  // default is Orange color theme
                }
            }
        }
        return instance;
    }

    public void setColorTheme(Theme theme) {
        this.theme = theme;
        BORDER_COLOR = theme.BORDER_COLOR;
        LIVE_COLOR = theme.LIVE_COLOR;
        NEIGHBOR_BORDER_COLOR = theme.NEIGHBOR_BORDER_COLOR;
        DEAD_COLOR = theme.DEAD_COLOR;
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
