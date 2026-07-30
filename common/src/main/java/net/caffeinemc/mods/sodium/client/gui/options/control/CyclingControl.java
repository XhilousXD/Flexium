package net.caffeinemc.mods.sodium.client.gui.options.control;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.gui.ColorTheme;
import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.caffeinemc.mods.sodium.client.gui.Layout;
import net.caffeinemc.mods.sodium.client.util.Dim2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

public class CyclingControl<T extends Enum<T>> implements Control {
    private final EnumOption<T> option;

    public CyclingControl(EnumOption<T> option, Class<T> enumType) {
        T[] universe = enumType.getEnumConstants();

        Validate.notEmpty(universe, "The enum universe must contain at least one item");

        this.option = option;
    }

    @Override
    public Option getOption() {
        return this.option;
    }

    @Override
    public ControlElement createElement(Screen screen, AbstractOptionList list, Dim2i dim, ColorTheme theme) {
        return new CyclingControlElement<>(list, this.option, dim, theme);
    }

    @Override
    public int getMaxWidth() {
        return Layout.CYCLING_CONTROL_WIDTH;
    }

    private static class CyclingControlElement<T extends Enum<T>> extends StatefulControlElement {
        private final EnumOption<T> option;
        private final T[] baseValues;

        public CyclingControlElement(AbstractOptionList list, EnumOption<T> option, Dim2i dim, ColorTheme theme) {
            super(list, dim, theme);

            this.option = option;
            this.baseValues = option.enumClass.getEnumConstants();
        }

        @Override
        public EnumOption<T> getOption() {
            return this.option;
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            super.extractRenderState(graphics, mouseX, mouseY, delta);

            if (this.option.shouldHideControl() || this.isResetOverlayActive()) {
                return;
            }

            var value = this.option.getValidatedValue();
            Component name = this.option.getElementName(value);

            int textWidth = this.getStringWidth(name);
            final int chevronW = 10; // width for "v" chevron
            int boxWidth = Math.max(90, textWidth + 20 + chevronW);
            int boxHeight = 16;
            int x = this.getLimitX() - Layout.OPTION_TEXT_SIDE_PADDING - boxWidth;
            int y = this.getCenterY() - boxHeight / 2;

            boolean hov = this.isHovered();

            // ── Dropdown box background ────────────────────────────────────────
            this.drawRect(graphics, x, y, x + boxWidth, y + boxHeight,
                    hov ? Colors.CARD_BG_HOVER : Colors.DROPDOWN_BG);
            this.drawBorder(graphics, x, y, x + boxWidth, y + boxHeight,
                    hov ? 0xFF6040A0 : Colors.DROPDOWN_BORDER);

            // ── Value text ─────────────────────────────────────────────────────
            int textX = x + 8;
            int textY = y + (boxHeight - this.font.lineHeight) / 2 + 1;
            int textColor = this.option.isEnabled() ? Colors.FOREGROUND : Colors.FOREGROUND_DISABLED;
            this.drawString(graphics, name, textX, textY, textColor);

            // ── Chevron "v" arrow on right side ────────────────────────────────
            int chevX = x + boxWidth - chevronW - 2;
            int chevColor = hov ? 0xFFAB94E4 : 0xFF888898;
            this.drawString(graphics, "\u25BE", chevX, textY, chevColor);

            if (this.isHovered()) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (super.mouseClicked(event, doubleClick)) return true;
            if (this.isResetOverlayActive()) return false;

            if (this.option.isEnabled() && event.button() == 0 && this.isMouseOver(event.x(), event.y())) {
                this.cycleControl(Minecraft.getInstance().hasShiftDown());
                return true;
            }

            return false;
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (!this.isFocused()) return false;

            if (event.isSelection()) {
                this.cycleControl(Minecraft.getInstance().hasShiftDown());
                return true;
            }

            return false;
        }

        private void cycleControl(boolean reverse) {
            this.playClickSound();

            var currentValue = this.option.getValidatedValue();
            int startIndex = 0;
            for (; startIndex < this.baseValues.length; startIndex++) {
                if (this.baseValues[startIndex] == currentValue) {
                    break;
                }
            }

            // step through values in the specified direction until a valid one is found
            var currentIndex = startIndex;
            do {
                if (reverse) {
                    currentIndex = (currentIndex + this.baseValues.length - 1) % this.baseValues.length;
                } else {
                    currentIndex = (currentIndex + 1) % this.baseValues.length;
                }

                currentValue = this.baseValues[currentIndex];
            } while (!this.option.isValueAllowed(currentValue));
            this.option.modifyValue(currentValue);
        }
    }
}
