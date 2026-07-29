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

            final int boxWidth = 44;
            final int boxHeight = 18;
            final int x = this.getLimitX() - Layout.OPTION_TEXT_SIDE_PADDING - boxWidth;
            final int y = this.getCenterY() - boxHeight / 2;
            final int xEnd = x + boxWidth;
            final int yEnd = y + boxHeight;

            final boolean enabled = this.option.isEnabled();
            final boolean ticked = this.option.getValidatedValue();

            final int bgColor = enabled ? (ticked ? Colors.TOGGLE_ON : Colors.TOGGLE_OFF) : 0xFF1E1E22;
            final int borderColor = enabled ? (ticked ? Colors.THEME_LIGHTER : 0xFF3F3F46) : 0xFF2F2F36;
            final int textColor = enabled ? (ticked ? Colors.FOREGROUND : Colors.FOREGROUND_DISABLED) : Colors.FOREGROUND_DISABLED;

            this.drawRect(graphics, x, y, xEnd, yEnd, bgColor);
            this.drawBorder(graphics, x, y, xEnd, yEnd, borderColor);

            String text = ticked ? "ON" : "OFF";
            int textWidth = this.font.width(text);
            this.drawString(graphics, text, x + (boxWidth - textWidth) / 2, y + (boxHeight - this.font.lineHeight) / 2 + 1, textColor);

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
