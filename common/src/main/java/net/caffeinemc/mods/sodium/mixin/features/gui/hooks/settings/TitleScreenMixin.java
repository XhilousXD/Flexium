package net.caffeinemc.mods.sodium.mixin.features.gui.hooks.settings;

import net.caffeinemc.mods.sodium.client.gui.Colors;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
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
        int centerH = 24;
        int centerX = (this.width - centerW) / 2;
        int startY = 120;
        int spacing = 28;

        // ── Main Menu Buttons (Center Stack) ──────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("\uD83F\uDC64  Singleplayer"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new SelectWorldScreen(this));
            }
        }).bounds(centerX, startY, centerW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("\uD83F\uDC65  Multiplayer"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new JoinMultiplayerScreen(this));
            }
        }).bounds(centerX, startY + spacing, centerW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDCDC  Minecraft Realms"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new JoinMultiplayerScreen(this));
            }
        }).bounds(centerX, startY + spacing * 2, centerW, centerH).build());

        // Side-by-side bottom center buttons
        int halfW = (centerW - 6) / 2;
        this.addRenderableWidget(Button.builder(Component.literal("\u2699  Options..."), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(VideoSettingsScreen.createScreen(this));
            }
        }).bounds(centerX, startY + spacing * 3, halfW, centerH).build());

        this.addRenderableWidget(Button.builder(Component.literal("\u23FB  Quit Game"), b -> {
            if (this.minecraft != null) {
                this.minecraft.stop();
            }
        }).bounds(centerX + halfW + 6, startY + spacing * 3, halfW, centerH).build());

        // ── Top Right Action Nav Buttons ──────────────────────────────────────
        int navW = 85;
        int navH = 20;
        int navY = 7;
        int navRightX = this.width - 12;

        int quitX = navRightX - 75;
        this.addRenderableWidget(Button.builder(Component.literal("\u23FB Quit Game"), b -> {
            if (this.minecraft != null) this.minecraft.stop();
        }).bounds(quitX, navY, 75, navH).build());

        int profileX = quitX - navW - 6;
        this.addRenderableWidget(Button.builder(Component.literal("\uD83F\uDC64 Profile"), b -> {}).bounds(profileX, navY, navW, navH).build());

        int settingsX = profileX - navW - 6;
        this.addRenderableWidget(Button.builder(Component.literal("\u2699 Settings"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(VideoSettingsScreen.createScreen(this));
            }
        }).bounds(settingsX, navY, navW, navH).build());

        int modMenuX = settingsX - navW - 6;
        this.addRenderableWidget(Button.builder(Component.literal("\u229E Mod Menu"), b -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(VideoSettingsScreen.createScreen(this));
            }
        }).bounds(modMenuX, navY, navW, navH).build());

        // ── Bottom Social Buttons ─────────────────────────────────────────────
        int socialW = 28;
        int socialH = 20;
        int socialY = this.height - 24;
        int socialCenterX = (this.width - (socialW * 3 + 12)) / 2;

        this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDCAC"), b -> {
            Util.getPlatform().openUri("https://caffeinemc.net/discord");
        }).bounds(socialCenterX, socialY, socialW, socialH).build());

        this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDC19"), b -> {
            Util.getPlatform().openUri("https://github.com/XhilousXD/Flexium");
        }).bounds(socialCenterX + socialW + 6, socialY, socialW, socialH).build());

        this.addRenderableWidget(Button.builder(Component.literal("\uD83C\uDF10"), b -> {
            Util.getPlatform().openUri("https://modrinth.com/mod/flexium");
        }).bounds(socialCenterX + (socialW + 6) * 2, socialY, socialW, socialH).build());

        // ── Open Changelog Button ─────────────────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDCC4 Open Changelog"), b -> {}).bounds(16, 260, 120, 20).build());

        // ── Check for Updates Button ──────────────────────────────────────────
        this.addRenderableWidget(Button.builder(Component.literal("\uD83D\uDD04 Check for Updates \u25CF"), b -> {}).bounds(this.width - 145, socialY, 135, socialH).build());
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
        int bottomBarH = 28;

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
        int cardY = headerH + 10;
        int cardW = 160;
        int card1H = 92;

        graphics.fill(cardX, cardY, cardX + cardW, cardY + card1H, 0xD01C1C22);
        this.drawBorder(graphics, cardX, cardY, cardW, card1H, 0x603C3C48);

        graphics.text(this.font, Component.literal("\u26A1 FLEXIUM STATUS"), cardX + 8, cardY + 6, 0xFFAB94E4);

        graphics.text(this.font, Component.literal("\u26A1 Optimization:"), cardX + 8, cardY + 20, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Active"), cardX + 92, cardY + 20, 0xFF4EF087);

        graphics.text(this.font, Component.literal("\u2699 Render Mode:"), cardX + 8, cardY + 34, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Vulkan"), cardX + 92, cardY + 34, 0xFFFFFFFF);

        graphics.text(this.font, Component.literal("\u2756 FPS Boost:"), cardX + 8, cardY + 48, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal("Enabled"), cardX + 92, cardY + 48, 0xFFFFFFFF);

        // Dynamic Memory Usage calculation
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        int ramPercent = maxMem > 0 ? (int) ((usedMem * 100) / maxMem) : 42;

        graphics.text(this.font, Component.literal("\uD83D\uDCBE Memory Usage:"), cardX + 8, cardY + 62, 0xFFCCCCCC);
        graphics.text(this.font, Component.literal(ramPercent + "%"), cardX + 110, cardY + 62, 0xFFFFFFFF);

        // Memory Progress Bar
        int barX = cardX + 8;
        int barY = cardY + 76;
        int barW = cardW - 16;
        int barH = 5;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0xFF2A2A34);
        int fillW = Math.max(4, (barW * ramPercent) / 100);
        graphics.fill(barX, barY, barX + fillW, barY + barH, 0xFF7B4EC9);

        // ── Left Card 2: NEWS FEED ────────────────────────────────────────────
        int card2Y = cardY + card1H + 8;
        int card2H = 138;

        graphics.fill(cardX, card2Y, cardX + cardW, card2Y + card2H, 0xD01C1C22);
        this.drawBorder(graphics, cardX, card2Y, cardW, card2H, 0x603C3C48);

        graphics.text(this.font, Component.literal("NEWS FEED"), cardX + 8, card2Y + 6, 0xFFAB94E4);

        // News item 1
        graphics.fill(cardX + 8, card2Y + 20, cardX + 28, card2Y + 40, 0xFF5D2E8E);
        graphics.text(this.font, Component.literal("F"), cardX + 14, card2Y + 26, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Flexium 1.0.0 Released!"), cardX + 32, card2Y + 20, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Performance improvements"), cardX + 32, card2Y + 30, 0xFF888898);
        graphics.text(this.font, Component.literal("May 25, 2026"), cardX + 32, card2Y + 38, 0xFF666678);

        // News item 2
        graphics.fill(cardX + 8, card2Y + 56, cardX + 28, card2Y + 76, 0xFF2A4A3A);
        graphics.text(this.font, Component.literal("Improved Chunk Culling"), cardX + 32, card2Y + 56, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Better chunk management"), cardX + 32, card2Y + 66, 0xFF888898);
        graphics.text(this.font, Component.literal("May 20, 2026"), cardX + 32, card2Y + 74, 0xFF666678);

        // News item 3
        graphics.fill(cardX + 8, card2Y + 92, cardX + 28, card2Y + 112, 0xFF4A2A4A);
        graphics.text(this.font, Component.literal("Vulkan Renderer Update"), cardX + 32, card2Y + 92, 0xFFFFFFFF);
        graphics.text(this.font, Component.literal("Enhanced stability"), cardX + 32, card2Y + 102, 0xFF888898);
        graphics.text(this.font, Component.literal("May 15, 2026"), cardX + 32, card2Y + 110, 0xFF666678);

        // ── Splash Text under Minecraft Logo ──────────────────────────────────
        String splash = "Optimized. Faster. Better.";
        int splashW = this.font.width(splash);
        int splashX = (this.width - splashW) / 2;
        graphics.text(this.font, Component.literal(splash), splashX, 98, 0xFFFFFF55);

        // ── Bottom Footer Bar ─────────────────────────────────────────────────
        int botY = this.height - bottomBarH;
        graphics.fill(0, botY, this.width, this.height, Colors.BOTTOM_BAR_BG);
        graphics.fill(0, botY, this.width, botY + 1, 0x40AB94E4);

        int botTextY = botY + (bottomBarH - this.font.lineHeight) / 2;
        graphics.text(this.font, Component.literal("Minecraft 26.1.2"), 12, botTextY, 0xFF888898);
    }
}
