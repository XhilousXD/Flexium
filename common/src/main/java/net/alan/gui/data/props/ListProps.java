package net.alan.gui.data.props;

import net.alan.gui.data.style.TextureSet;

public record ListProps(int gap, String itemWidth, String itemHeight) {
    public record TrackDef(TextureSet texture, String color) {}
    public record ThumbDef(TextureSet texture, String color) {}
    public record ScrollbarDef(int width, String x, String y, TrackDef track, ThumbDef thumb) {}
}
