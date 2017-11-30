package com.holub.ui;

import java.awt.Color;

public class OrangeColorTheme extends Theme {
    public OrangeColorTheme() {
        setColor();
    }

    @Override
    void setColor() {
        BORDER_COLOR = Colors.DARK_YELLOW;
        LIVE_COLOR = Color.RED;
        DEAD_COLOR = Colors.LIGHT_YELLOW;
        NEIGHBOR_BORDER_COLOR = Colors.DARK_ORANGE;
        ACTIVE_BORDER_COLOR = Color.BLUE;
    }

}
