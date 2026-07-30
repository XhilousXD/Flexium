package net.alan.gui.elements;

import net.alan.gui.context.ScreenVariableRegistry;
import net.alan.gui.data.config.ScreenLayout;
import net.alan.gui.render.JsonScreenRenderer;
import net.alan.gui.util.JsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.IdentityHashMap;

public class JsonScreen extends Screen {
    private final Screen lastScreen;
    private final Identifier layoutId;
    private JsonScreenRenderer renderer;

    public JsonScreen(Identifier layoutId) {
        this(null, layoutId);
    }

    public JsonScreen(Screen lastScreen, Identifier layoutId) {
        super(Component.translatable("gui.sirius_ui.title"));
        this.lastScreen = lastScreen;
        this.layoutId = layoutId;

        Minecraft minecraft = Minecraft.getInstance();
        ScreenLayout layout = JsonLoader.loadScreenLayout(minecraft.getResourceManager(), layoutId);
        if (layout != null) {
            String screenId = ScreenVariableRegistry.extractScreenId(layoutId);
            this.renderer = new JsonScreenRenderer(minecraft, this, layout, screenId);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (renderer != null) {
            renderer.render(graphics, mouseX, mouseY, delta);
        } else {
            super.extractRenderState(graphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (renderer != null && renderer.mouseClicked(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (renderer != null && renderer.mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (renderer != null && renderer.mouseDragged(event.x(), event.y(), event.button(), dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (renderer != null && renderer.keyPressed(event.key(), 0, event.modifiers())) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (renderer != null && renderer.charTyped((char) event.codepoint(), 0)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        minecraft.options.save();
        Screen root = findRootScreen(lastScreen);
        if (root != null) {
            minecraft.setScreen(root);
        } else {
            super.onClose();
        }
    }

    private Screen findRootScreen(Screen screen) {
        IdentityHashMap<Screen, Boolean> visited = new IdentityHashMap<>();
        Screen current = screen;
        while (current instanceof JsonScreen jsonScreen) {
            if (visited.containsKey(current)) break;
            visited.put(current, true);
            current = jsonScreen.lastScreen;
        }
        return current;
    }

    public Identifier getLayoutId() {
        return layoutId;
    }

    public Screen getLastScreen() {
        return lastScreen;
    }
}