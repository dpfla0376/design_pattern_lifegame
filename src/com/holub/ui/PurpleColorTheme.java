package com.holub.ui;

import java.awt.*;

public class PurpleColorTheme extends Theme {

    public PurpleColorTheme() {
        setColor();
    }

    @Override
    void setColor() {
        BORDER_COLOR = Colors.LIGHT_PURPLE;
        LIVE_COLOR = Colors.MEDIUM_PURPLE;
        DEAD_COLOR = Color.WHITE;
        NEIGHBOR_BORDER_COLOR = Colors.DARK_PURPLE;
        ACTIVE_BORDER_COLOR = Color.BLUE;
    }
}
