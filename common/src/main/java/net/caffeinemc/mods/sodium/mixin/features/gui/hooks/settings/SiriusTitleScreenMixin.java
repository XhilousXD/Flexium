package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.settings;

import net.alan.gui.Config;
import net.alan.gui.Main;
import net.alan.gui.context.ScreenVariableRegistry;
import net.alan.gui.data.config.ScreenLayout;
import net.alan.gui.registry.JsonScreenRegistry;
import net.alan.gui.render.JsonScreenRenderer;
import net.alan.gui.util.JsonLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(TitleScreen.class)
public abstract class SiriusTitleScreenMixin extends Screen {
    @Unique
    private JsonScreenRenderer sirius$uiRenderer;

    protected SiriusTitleScreenMixin(net.minecraft.network.chat.Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void sirius$onInit(CallbackInfo ci) {
        if (!Config.ENABLE_CUSTOM_UI) return;
        Minecraft client = Minecraft.getInstance();
        ResourceManager rm = client.getResourceManager();

        Identifier layoutId = JsonScreenRegistry.getLayoutId("titleScreen")
                .orElse(Identifier.fromNamespaceAndPath(Main.MOD_ID, "screens/title_screen.json"));
        ScreenLayout layout = JsonLoader.loadScreenLayout(rm, layoutId);

        if (layout != null) {
            String screenId = ScreenVariableRegistry.extractScreenId(layoutId);
            this.sirius$uiRenderer = new JsonScreenRenderer(client, (TitleScreen) (Object) this, layout, screenId);
            clearOriginalWidgets();
        }
    }

    @Unique
    private void clearOriginalWidgets() {
        try {
            this.children().clear();
            Field renderablesField = Screen.class.getDeclaredField("renderables");
            renderablesField.setAccessible(true);
            List<?> renderables = (List<?>) renderablesField.get(this);
            renderables.clear();
        } catch (Exception ignored) {}
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void sirius$onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (sirius$uiRenderer != null) {
            sirius$uiRenderer.render(graphics, mouseX, mouseY, delta);
            ci.cancel();
        }
    }
}
