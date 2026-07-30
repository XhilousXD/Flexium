package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.settings;

import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        this.clearWidgets();

        int centerW = 220;
        int centerH = 22;
        int centerX = (this.width - centerW) / 2;
        int startY = 118;
        int spacing = 26;

        // ── Main Menu Buttons (Center Stack) ──────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("Singleplayer"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SelectWorldScreen(this));
            }
        }).bounds(centerX, startY, centerW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("Multiplayer"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new JoinMultiplayerScreen(this));
            }
        }).bounds(centerX, startY + spacing, centerW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("Minecraft Realms"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new JoinMultiplayerScreen(this));
            }
        }).bounds(centerX, startY + spacing * 2, centerW, centerH).build());

        // Side-by-side bottom center buttons
        int halfW = (centerW - 6) / 2;
        this.addRenderableWidget(Button.builder(Component.literal("Options..."), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options, false));
            }
        }).bounds(centerX, startY + spacing * 3, halfW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("Quit Game"), b -> {
            if (this.minecraft != null) {
                this.minecraft.stop();
            }
        }).bounds(centerX + halfW + 6, startY + spacing * 3, halfW, centerH).build());

        // ── Top Right Action Nav Buttons ──────────────────────────────────────
        int navW = 80;
        int navH = 18;
        int navY = 7;
        int navRightX = this.width - 10;

        int quitX = navRightX - 70;
        this.addRenderableWidget(Button.builder(Component.literal("Quit Game"), b -> {
            if (this.minecraft != null) this.minecraft.stop();
        }).bounds(quitX, navY, 70, navH).build());

        int profileX = quitX - navW - 4;
        this.addRenderableWidget(Button.builder(Component.literal("Profile"), b -> {}).bounds(profileX, navY, navW, navH).build());

        int settingsX = profileX - navW - 4;
        this.addRenderableWidget(Button.builder(Component.literal("Settings"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(VideoSettingsScreen.createScreen(this));
            }
        }).bounds(settingsX, navY, navW, navH).build());

        int modMenuX = settingsX - navW - 4;
        this.addRenderableWidget(Button.builder(Component.literal("Mod Menu"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(VideoSettingsScreen.createScreen(this));
            }
        }).bounds(modMenuX, navY, navW, navH).build());

        // ── Bottom Social Buttons ─────────────────────────────────────────────
        int socialW = 40;
        int socialH = 18;
        int socialY = this.height - 23;
        int socialCenterX = (this.width - (socialW * 3 + 8)) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Discord"), b -> {
            Util.getPlatform().openUri("https://caffeinemc.net/discord");
        }).bounds(socialCenterX, socialY, socialW, socialH).build());

        this.addRenderableWidget(Button.builder(Component.literal("GitHub"), b -> {
            Util.getPlatform().openUri("https://github.com/XhilousXD/Flexium");
        }).bounds(socialCenterX + socialW + 4, socialY, socialW, socialH).build());

        this.addRenderableWidget(Button.builder(Component.literal("Web"), b -> {
            Util.getPlatform().openUri("https://modrinth.com/mod/flexium");
        }).bounds(socialCenterX + (socialW + 4) * 2, socialY, socialW, socialH).build());

        // ── Open Changelog Button ─────────────────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("Open Changelog"), b -> {}).bounds(14, 232, 148, 18).build());

        // ── Check for Updates Button ──────────────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("Check for Updates \u25CF"), b -> {}).bounds(this.width - 138, socialY, 130, socialH).build());
    }

    private void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int headerH = 34;
        int bottomBarH = 26;

        // ── Top Header Bar ───────────────────────────────────────────────────
        graphics.fill(0, 0, this.width, headerH, Colors.HEADER_BG);
        graphics.fill(0, headerH - 1, this.width, headerH, 0x50AB94E4);

        // Brushy F logo box
        int logoBoxX = 10;
        int logoBoxY = (headerH - 20) / 2;
        graphics.fill(logoBoxX, logoBoxY, logoBoxX + 22, logoBoxY + 20, 0xFF5D2E8E);
        graphics.fill(logoBoxX + 1, logoBoxY + 1, logoBoxX + 21, logoBoxY + 19, 0xFF7040B8);
        int fX = logoBoxX + (22 - this.font.width("F")) / 2;
        int fY = logoBoxY + (20 - this.font.lineHeight) / 2;
        graphics.text(this.font, Component.literal("F"), fX, fY, 0xFFFFFFFF);

        // Header Title: FLEXIUM 1.0.0 Optimization Mod
        int titleX = logoBoxX + 28;
        int titleY = (headerH - this.font.lineHeight) / 2;
        graphics.text(this.font, Component.literal("FLEXIUM"), titleX, titleY, 0xFFFFFFFF);

        int verBadgeX = titleX + this.font.width("FLEXIUM") + 6;
        graphics.fill(verBadgeX, titleY - 1, verBadgeX + this.font.width("1.0.0") + 6, titleY + this.font.lineHeight + 1, 0xFF2E2E3A);
        graphics.text(this.font, Component.literal("1.0.0"), verBadgeX + 3, titleY, 0xFFAB94E4);

        int subX = verBadgeX + this.font.width("1.0.0") + 12;
        graphics.text(this.font, Component.literal("Optimization Mod"), subX, titleY, 0xFF8870C8);

        // ── Left Card 1: FLEXIUM STATUS ───────────────────────────────────────
        int cardX = 14;
        int cardY = headerH + 8;
        int cardW = 148;
        int card1H = 78;

        graphics.fill(cardX, cardY, cardX + cardW, cardY + card1H, 0xD01C1C22);
        this.drawBorder(graphics, cardX, cardY, cardW, card1H, 0x603C3C48);

        graphics.text(this.font, Component.literal("FLEXIUM STATUS"), cardX + 6, cardY + 5, 0xFFAB94E4);

        graphics.text(this.font, Component.literal("Optimization:"), cardX + 6, cardY + 18, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Active"), cardX + 85, cardY + 18, 0xFF4EF087);

        graphics.text(this.font, Component.literal("Render Mode:"), cardX + 6, cardY + 30, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Vulkan"), cardX + 85, cardY + 30, 0xFFFFFFFF);

        graphics.text(this.font, Component.literal("FPS Boost:"), cardX + 6, cardY + 42, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Enabled"), cardX + 85, cardY + 42, 0xFFFFFFFF);

        // Dynamic Memory Usage calculation
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        int ramPercent = maxMem > 0 ? (int) ((usedMem * 100) / maxMem) : 42;

        graphics.text(this.font, Component.literal("Memory Usage:"), cardX + 6, cardY + 54, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal(ramPercent + "%"), cardX + 100, cardY + 54, 0xFFFFFFFF);

        // Memory Progress Bar
        int barX = cardX + 6;
        int barY = cardY + 66;
        int barW = cardW - 12;
        int barH = 4;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0xFF2A2A34);
        int fillW = Math.max(4, (barW * ramPercent) / 100);
        graphics.fill(barX, barY, barX + fillW, barY + barH, 0xFF7B4EC9);

        // ── Left Card 2: NEWS FEED ────────────────────────────────────────────
        int card2Y = cardY + card1H + 6;
        int card2H = 96;

        graphics.fill(cardX, card2Y, cardX + cardW, card2Y + card2H, 0xD01C1C22);
        this.drawBorder(graphics, cardX, card2Y, cardW, card2H, 0x603C3C48);

        graphics.text(this.font, Component.literal("NEWS FEED"), cardX + 6, card2Y + 5, 0xFFAB94E4);

        // News item 1
        graphics.fill(cardX + 6, card2Y + 18, cardX + 22, card2Y + 34, 0xFF5D2E8E);
        graphics.text(this.font, Component.literal("F"), cardX + 11, card2Y + 22, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Flexium 1.0.0 Released"), cardX + 26, card2Y + 18, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Performance boost"), cardX + 26, card2Y + 26, 0xFF888898);

        // News item 2
        graphics.fill(cardX + 6, card2Y + 44, cardX + 22, card2Y + 60, 0xFF2A4A3A);
        graphics.text(this.font, Component.literal("Improved Chunk Culling"), cardX + 26, card2Y + 44, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Higher FPS management"), cardX + 26, card2Y + 52, 0xFF888898);

        // News item 3
        graphics.fill(cardX + 6, card2Y + 70, cardX + 22, card2Y + 86, 0xFF4A2A4A);
        graphics.text(this.font, Component.literal("Vulkan Renderer Update"), cardX + 26, card2Y + 70, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Enhanced stability"), cardX + 26, card2Y + 78, 0xFF888898);

        // ── Splash Text under Minecraft Logo ──────────────────────────────────
        String splash = "Optimized. Faster. Better.";
        int splashW = this.font.width(splash);
        int splashX = (this.width - splashW) / 2;
        graphics.text(this.font, Component.literal(splash), splashX, 102, 0xFFFFFF55);

        // ── Bottom Footer Bar ─────────────────────────────────────────────────
        int botY = this.height - bottomBarH;
        graphics.fill(0, botY, this.width, this.height, Colors.BOTTOM_BAR_BG);
        graphics.fill(0, botY, this.width, botY + 1, 0x40AB94E4);

        int botTextY = botY + (bottomBarH - this.font.lineHeight) / 2;
        graphics.text(this.font, Component.literal("Minecraft 26.1.2"), 12, botTextY, 0xFF888898);
    }
}
