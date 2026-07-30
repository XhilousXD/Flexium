package net.alan.gui.render;

import net.alan.gui.data.CycleValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;

public class OptionBinder {

    public static void syncSlider(String optionKey, double ratio,
                                  double min, double max,
                                  Map<String, String> vars, Options opts) {
        double value = min + Mth.clamp(ratio, 0.0, 1.0) * (max - min);
        setOptionValue(optionKey, value, opts);
        vars.put("display_value", formatValue(optionKey, value));
    }

    @Deprecated
    public static void syncSlider(String optionKey, double ratio,
                                  double min, double max,
                                  Map<String, String> vars) {
        syncSlider(optionKey, ratio, min, max, vars, Minecraft.getInstance().options);
    }

    public static double getSliderRatio(String optionKey, double min, double max, Options opts) {
        double cur = getOptionValue(optionKey, opts);
        if (max <= min) return 0.0;
        return Mth.clamp((cur - min) / (max - min), 0.0, 1.0);
    }

    public static String formatValue(String key, double val) {
        return switch (key) {
            case "fov" -> String.format("%.0f°", val);
            case "gamma", "brightness" -> val <= 0.0 ? "0%" : String.format("%.0f%%", val * 100);
            case "music", "master", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice" ->
                    String.format("%.0f%%", val * 100);
            case "renderDistance" -> String.format("%d chunks", (int) Math.round(val));
            case "framerateLimit" -> (int) Math.round(val) >= 260 ? "Unlimited" : String.format("%d fps", (int) Math.round(val));
            case "mouseSensitivity" -> String.format("%.0f%%", val * 200);
            default -> String.format("%.2f", val);
        };
    }

    public static double getOptionRawValue(String key, Options opts) {
        return getOptionValue(key, opts);
    }

    public static void saveOptions(Options opts) {
        try {
            opts.save();
        } catch (Exception ignored) {}
    }

    public static double getOptionValue(String key, Options opts) {
        try {
            return switch (key) {
                case "fov" -> opts.fov().get();
                case "gamma", "brightness" -> opts.gamma().get();
                case "renderDistance" -> opts.renderDistance().get();
                case "simulationDistance" -> opts.simulationDistance().get();
                case "framerateLimit" -> opts.framerateLimit().get();
                case "mouseSensitivity" -> opts.sensitivity().get();
                default -> 0.5;
            };
        } catch (Exception e) {
            return 0.5;
        }
    }

    public static void setOptionValue(String key, double value, Options opts) {
        try {
            switch (key) {
                case "fov" -> opts.fov().set((int) Math.round(value));
                case "gamma", "brightness" -> opts.gamma().set(value);
                case "renderDistance" -> opts.renderDistance().set((int) Math.round(value));
                case "simulationDistance" -> opts.simulationDistance().set((int) Math.round(value));
                case "framerateLimit" -> opts.framerateLimit().set((int) Math.round(value));
                case "mouseSensitivity" -> opts.sensitivity().set(value);
            }
        } catch (Exception ignored) {}
    }

    public static int getOptionIndex(String key, Options opts) { return 0; }

    // ========== CycleButton 绑定 ==========

    private static String getCurrentOptionKey(String key, Options opts) {
        try {
            Minecraft mc = Minecraft.getInstance();
            return switch (key) {
                case "difficulty" -> mc.level != null ? mc.level.getDifficulty().name().toLowerCase() : "normal";
                case "soundDevice" -> opts.soundDevice().get() != null ? opts.soundDevice().get() : "";
                case "hideServerAddress" -> String.valueOf(opts.hideServerAddress).toLowerCase();
                case "advancedItemTooltips" -> String.valueOf(opts.advancedItemTooltips).toLowerCase();
                case "pauseOnLostFocus" -> String.valueOf(opts.pauseOnLostFocus).toLowerCase();
                default -> "0";
            };
        } catch (Exception e) {
            return "0";
        }
    }

    public static int getCycleOptionIndex(String key, List<CycleValue> values, Options opts) {
        String currentKey = getCurrentOptionKey(key, opts);
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).key().equalsIgnoreCase(currentKey)) return i;
        }
        return 0;
    }

    public static void setCycleOptionValue(String key, String valueKey, Options opts) {
        try {
            switch (key) {
                case "hideServerAddress" -> opts.hideServerAddress = Boolean.parseBoolean(valueKey);
                case "advancedItemTooltips" -> opts.advancedItemTooltips = Boolean.parseBoolean(valueKey);
                case "pauseOnLostFocus" -> opts.pauseOnLostFocus = Boolean.parseBoolean(valueKey);
                case "soundDevice" -> opts.soundDevice().set(valueKey);
            }
        } catch (Exception ignored) {}
    }

    public static void resetOptionToDefault(String key, Options opts) {
        try {
            switch (key) {
                case "hideServerAddress" -> opts.hideServerAddress = false;
                case "advancedItemTooltips" -> opts.advancedItemTooltips = false;
                case "pauseOnLostFocus" -> opts.pauseOnLostFocus = true;
            }
        } catch (Exception ignored) {}
    }
}