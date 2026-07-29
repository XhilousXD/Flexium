package net.caffeinemc.mods.sodium.client.gui;

import net.caffeinemc.mods.sodium.api.util.ColorARGB;
import net.minecraft.util.Mth;

// colors in ARGB format
public class Colors {
    public static final int THEME = 0xFFAB94E4;
    public static final int THEME_LIGHTER = 0xFFD4CCFD;
    public static final int THEME_DARKER = 0xFF7A6B9E;
    public static final int FOREGROUND = 0xFFFFFFFF;
    public static final int FOREGROUND_DISABLED = 0xFFAAAAAA;

    public static final int BACKGROUND_LIGHT = 0x40000000;
    public static final int BACKGROUND_MEDIUM = 0x60000000;
    public static final int BACKGROUND_HOVER = 0xE0000000;
    public static final int BACKGROUND_OVERLAY = 0xEA000000;
    public static final int BACKGROUND_DEFAULT = 0x90000000;
    public static final int BACKGROUND_DARKER = 0xB0000000;
    public static final int BACKGROUND_HIGHLIGHT = 0x08FFFFFF;

    public static final int BUTTON_BORDER = 0x809B6BFF;

    // Fluxis / Flexium UI Theme Palette
    public static final int TAB_ACTIVE_BG = 0xFF5D2E8E;
    public static final int TAB_ACTIVE_BORDER = 0xFFAB94E4;
    public static final int HEADER_BG = 0xFA121215;
    public static final int PANEL_BG = 0xF0171719;
    public static final int CARD_BG = 0xD01F1F23;
    public static final int CARD_BORDER = 0x603C3C44;
    public static final int TOGGLE_ON = 0xFF6C3BB8;
    public static final int TOGGLE_OFF = 0xFF28282C;

    private static final float LIGHTEN_FACTOR = 0.3f;
    private static final float DARKEN_FACTOR = -0.23f;

    public static int darken(int color) {
        return adjust(color, DARKEN_FACTOR);
    }

    public static int lighten(int color) {
        return adjust(color, LIGHTEN_FACTOR);
    }

    public static int adjust(int color, float factor) {
        float[] hsv = ColorARGB.toHSV(color);
        var s = Mth.clamp(hsv[1] * (1 - Math.abs(factor)), 0, 1);
        var b = Mth.clamp(hsv[2] * (1 + factor), 0, 1);
        return ColorARGB.transferAlpha(ColorARGB.fromHSV(hsv[0], s, b), color);
    }

    public static int constrainColorHSV(int color, float minSaturation, float minBrightness) {
        float[] hsv = ColorARGB.toHSV(color);
        hsv[1] = Math.max(hsv[1], minSaturation);
        hsv[2] = Math.max(hsv[2], minBrightness);
        return ColorARGB.fromHSV(hsv);
    }
}
