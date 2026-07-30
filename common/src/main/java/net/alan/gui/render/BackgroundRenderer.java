package net.alan.gui.render;

import net.alan.gui.data.background.BackgroundLayer;
import net.alan.gui.data.background.PanoramaConfig;
import net.alan.gui.util.ExpressionEvaluator;
import net.alan.gui.util.GameStateProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackgroundRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundRenderer.class);
    private static final int DEFAULT_BG_WIDTH = 1920;
    private static final int DEFAULT_BG_HEIGHT = 1080;
    private final Minecraft minecraft;

    public BackgroundRenderer(Minecraft minecraft) { this.minecraft = minecraft; }

    public void render(GuiGraphicsExtractor graphics, int screenWidth, int screenHeight,
                       PanoramaConfig bgConfig, List<BackgroundLayer> layers, float delta) {
        if (bgConfig != null) {
            String rawType = bgConfig.getType();
            String type = evalStringExpr(rawType, screenWidth, screenHeight);
            switch (type) {
                case "image" -> {
                    String rawPath = bgConfig.getTexture();
                    String path = evalStringExpr(rawPath, screenWidth, screenHeight);
                    if (path != null && !path.isEmpty()) {
                        try {
                            Identifier id = Identifier.tryParse(path);
                            if (id != null) renderScaled(graphics, id, screenWidth, screenHeight);
                        } catch (Exception e) {
                            LOGGER.error("Failed to render image background {}: {}", path, e.getMessage());
                            graphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);
                        }
                    } else {
                        graphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);
                    }
                }
                default -> graphics.fill(0, 0, screenWidth, screenHeight, 0xFF101015);
            }
        } else {
            graphics.fill(0, 0, screenWidth, screenHeight, 0xFF101015);
        }

        if (layers != null) {
            Map<String, Integer> gameVars = injectGameStateVars();
            for (BackgroundLayer bg : layers) {
                try {
                    int w = ExpressionEvaluator.eval(bg.getWidth(), screenWidth, screenHeight, 0, 0, gameVars);
                    int h = ExpressionEvaluator.eval(bg.getHeight(), screenWidth, screenHeight, 0, 0, gameVars);
                    int x = ExpressionEvaluator.eval(bg.getX(), screenWidth, screenHeight, w, h, gameVars);
                    int y = ExpressionEvaluator.eval(bg.getY(), screenWidth, screenHeight, w, h, gameVars);
                    graphics.fill(x, y, x + w, y + h, parseColor(bg.getColor()));
                } catch (Exception e) {
                    LOGGER.warn("Failed to render background layer: {}", e.getMessage());
                }
            }
        }
    }

    private String evalStringExpr(String expr, int sw, int sh) {
        if (expr == null) return null;
        if (!expr.contains("$[")) return expr;
        Map<String, Integer> gameVars = injectGameStateVars();

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            int start = expr.indexOf("$[", i);
            if (start == -1) {
                sb.append(expr.substring(i));
                break;
            }
            sb.append(expr, i, start);
            int end = expr.indexOf("]", start);
            if (end == -1) {
                sb.append(expr.substring(start));
                break;
            }
            String subExpr = expr.substring(start + 2, end);
            try {
                int res = ExpressionEvaluator.eval(subExpr, sw, sh, 0, 0, gameVars);
                sb.append(res);
            } catch (Exception e) {
                sb.append(subExpr);
            }
            i = end + 1;
        }
        return sb.toString();
    }

    private void renderScaled(GuiGraphicsExtractor graphics, Identifier texture, int sw, int sh) {
        float texAspect = (float) DEFAULT_BG_WIDTH / DEFAULT_BG_HEIGHT;
        float screenAspect = (float) sw / sh;
        int rw, rh;
        if (screenAspect > texAspect) { rw = sw; rh = (int)(sw / texAspect); }
        else { rh = sh; rw = (int)(sh * texAspect); }
        int ox = (sw - rw) / 2, oy = (sh - rh) / 2;
        if (texture.getPath().contains("textures/") || texture.getPath().endsWith(".png")) {
    graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, ox, oy, 0.0F, 0.0F, rw, rh, rw, rh);
} else {
    graphics.blitSprite(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, ox, oy, rw, rh, -1);
}
    }

    public static int parseColor(String colorStr) {
        if (colorStr == null || colorStr.isEmpty()) return 0xFFFFFFFF;
        String trimmed = colorStr.trim();
        if (trimmed.startsWith("0x") || trimmed.startsWith("0X")) trimmed = trimmed.substring(2);
        else if (trimmed.startsWith("#")) trimmed = trimmed.substring(1);
        try {
            if (trimmed.length() == 6) {
                return (int) (0xFF000000L | Long.parseLong(trimmed, 16));
            } else if (trimmed.length() == 8) {
                return (int) Long.parseLong(trimmed, 16);
            }
        } catch (NumberFormatException ignored) {}
        return 0xFFFFFFFF;
    }

    private Map<String, Integer> injectGameStateVars() {
        Map<String, Integer> gameVars = new HashMap<>();
        try {
            GameStateProvider.injectScreenVariables(gameVars, minecraft);
        } catch (Exception e) {
            LOGGER.warn("Failed to inject game state variables: {}", e.getMessage());
        }
        return gameVars;
    }
}