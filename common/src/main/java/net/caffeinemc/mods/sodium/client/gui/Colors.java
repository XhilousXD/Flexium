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
    public static final int TAB_ACTIVE_BG    = 0xFF5D2E8E;  // active sidebar tab purple fill
    public static final int TAB_ACTIVE_BORDER = 0xFFAB94E4; // active tab border glow
    public static final int TAB_HOVER_BG     = 0x30AB94E4;  // hover sidebar tab
    public static final int HEADER_BG        = 0xFF0F0F11;  // top header dark bar
    public static final int SIDEBAR_BG       = 0xFF111114;  // left sidebar background
    public static final int PANEL_BG         = 0xFF18181C;  // main content area
    public static final int CARD_BG          = 0xFF1C1C20;  // option row card background
    public static final int CARD_BG_HOVER    = 0xFF232328;  // option row card hover
    public static final int CARD_BORDER      = 0x50444450;  // option row border
    public static final int SECTION_HEADER   = 0xFFAB94E4;  // section header text + icon
    public static final int SECTION_DIVIDER  = 0x40AB94E4;  // section header divider line
    public static final int TOGGLE_ON        = 0xFF7B4EC9;  // toggle pill ON (purple)
    public static final int TOGGLE_ON_KNOB   = 0xFFFFFFFF;  // toggle knob white
    public static final int TOGGLE_OFF       = 0xFF2A2A30;  // toggle pill OFF (dark)
    public static final int TOGGLE_OFF_KNOB  = 0xFF888898;  // toggle knob gray
    public static final int BOTTOM_BAR_BG    = 0xFF0E0E11;  // bottom action bar
    public static final int APPLY_BTN_BG     = 0xFF6032A8;  // apply button purple
    public static final int APPLY_BTN_HOVER  = 0xFF7040C0;  // apply button hover
    public static final int DISCARD_BTN_BG   = 0xFF1C1C22;  // discard/undo button dark
    public static final int DISCARD_BTN_BORDER = 0xFF3A3A48; // discard button border
    public static final int DROPDOWN_BG      = 0xFF1E1E24;  // dropdown box background
    public static final int DROPDOWN_BORDER  = 0xFF38384A;  // dropdown border

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
