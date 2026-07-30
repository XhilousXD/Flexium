package net.caffeinemc.mods.sodium.client.gui.options.control;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.caffeinemc.mods.sodium.client.gui.Layout;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public class TickBoxControl implements Control {
    private final BooleanOption option;

    public TickBoxControl(BooleanOption option) {
        this.option = option;
    }

    @Override
    public ControlElement createElement(Screen screen, AbstractOptionList list, Dim2i dim, ColorTheme theme) {
        return new TickBoxControlElement(list, this.option, dim, theme);
    }

    @Override
    public int getMaxWidth() {
        return Layout.TICKBOX_CONTROL_WIDTH;
    }

    @Override
    public StatefulOption<Boolean> getOption() {
        return this.option;
    }

    private static class TickBoxControlElement extends StatefulControlElement {
        private final BooleanOption option;

        public TickBoxControlElement(AbstractOptionList list, BooleanOption option, Dim2i dim, ColorTheme theme) {
            super(list, dim, theme);

            this.option = option;
        }

        @Override
        public BooleanOption getOption() {
            return this.option;
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            super.extractRenderState(graphics, mouseX, mouseY, delta);

            if (this.option.shouldHideControl() || this.isResetOverlayActive()) {
                return;
            }

            // Pill-shaped toggle dimensions  (matches screenshot exactly)
            final int pillW  = 46;
            final int pillH  = 16;
            final int knobSz = 10;

            final int x  = this.getLimitX() - Layout.OPTION_TEXT_SIDE_PADDING - pillW;
            final int y  = this.getCenterY() - pillH / 2;
            final int x2 = x + pillW;
            final int y2 = y + pillH;

            final boolean enabled = this.option.isEnabled();
            final boolean ticked  = this.option.getValidatedValue();

            // ── Pill background ───────────────────────────────────────────────
            int pillBg     = enabled ? (ticked ? Colors.TOGGLE_ON    : Colors.TOGGLE_OFF)     : 0xFF1E1E22;
            int pillBorder = enabled ? (ticked ? 0xFF9B6FE4          : 0xFF3A3A46)             : 0xFF2A2A30;
            int knobColor  = enabled ? (ticked ? Colors.TOGGLE_ON_KNOB : Colors.TOGGLE_OFF_KNOB) : 0xFF555560;

            this.drawRect(graphics, x, y, x2, y2, pillBg);
            this.drawBorder(graphics, x, y, x2, y2, pillBorder);

            // ── Sliding knob ─────────────────────────────────────────────────
            int knobX = ticked ? (x2 - knobSz - 3) : (x + 3);
            int knobY = y + (pillH - knobSz) / 2;
            this.drawRect(graphics, knobX, knobY, knobX + knobSz, knobY + knobSz, knobColor);

            // ── ON / OFF label ────────────────────────────────────────────────
            int labelColor = enabled ? (ticked ? 0xFFFFFFFF : Colors.FOREGROUND_DISABLED) : Colors.FOREGROUND_DISABLED;
            String text = ticked ? "ON" : "OFF";
            int textW = this.font.width(text);
            // position label on the opposite side of knob
            int labelX = ticked ? (x + 3) : (x2 - textW - 4);
            int labelY = y + (pillH - this.font.lineHeight) / 2 + 1;
            this.drawString(graphics, text, labelX, labelY, labelColor);

            if (this.isHovered()) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }


        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (super.mouseClicked(event, doubleClick)) return true;
            if (this.isResetOverlayActive()) return false;

            if (this.option.isEnabled() && event.button() == 0 && this.isMouseOver(event.x(), event.y())) {
                this.toggleControl();
                return true;
            }

            return false;
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!this.isFocused()) return false;

            if (event.isSelection()) {
                this.toggleControl();
                return true;
            }

            return false;
        }

        private void toggleControl() {
            this.playClickSound();

            this.option.modifyValue(!this.option.getValidatedValue());
        }
    }
}
