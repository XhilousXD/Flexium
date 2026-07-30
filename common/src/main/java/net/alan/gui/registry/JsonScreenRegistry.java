package net.alan.gui.registry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonScreenRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonScreenRegistry.class);
    private static final Identifier REGISTER_LOCATION = Identifier.withDefaultNamespace("alanht/register.json");
    private static final Gson GSON = new Gson();
    private static Map<String, Identifier> mappings = new HashMap<>();
    private static boolean loaded = false;

    public static void load(ResourceManager resourceManager) {
        mappings.clear();
        try {
            Optional<Resource> resource = resourceManager.getResource(REGISTER_LOCATION);
            if (resource.isEmpty()) {
                LOGGER.warn("register.json not found at {}", REGISTER_LOCATION);
                loaded = true;
                return;
            }
            try (Reader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
                Map<String, String> raw = GSON.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType());
                if (raw != null) {
                    raw.forEach((id, location) -> {
                        try {
                            Identifier layoutId = Identifier.parse(location);
                            mappings.put(id, layoutId);
                        } catch (Exception e) {
                            LOGGER.error("Invalid layout Identifier for screen '{}': {}", id, location, e);
                        }
                    });
                }
            }
            LOGGER.info("Loaded {} JSON screen mappings", mappings.size());
        } catch (Exception e) {
            LOGGER.error("Failed to load register.json", e);
        }
        loaded = true;
    }

    public static Optional<Identifier> getLayoutId(String screenId) {
        return Optional.ofNullable(mappings.get(screenId));
    }

    public static boolean isEmpty() { return mappings.isEmpty(); }
    public static boolean isLoaded() { return loaded; }
    public static Map<String, Identifier> getMappings() { return new HashMap<>(mappings); }
}