package com.holub.ui;

import java.awt.*;

public abstract class Theme {
    public Color BORDER_COLOR;
    public Color LIVE_COLOR;
    public Color DEAD_COLOR;
    public Color NEIGHBOR_BORDER_COLOR;
    public Color ACTIVE_BORDER_COLOR;
    abstract void setColor();
}
