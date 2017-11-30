package com.holub.ui;

import java.awt.*;

public class BlueColorTheme extends Theme {

    public BlueColorTheme() {
        setColor();
    }

    @Override
    void setColor() {
        BORDER_COLOR = Color.CYAN;
        LIVE_COLOR = Colors.MEDIUM_BLUE;
        DEAD_COLOR = Color.WHITE;
        NEIGHBOR_BORDER_COLOR = Colors.DARK_BLUE;
        ACTIVE_BORDER_COLOR = Color.BLUE;
    }
}
