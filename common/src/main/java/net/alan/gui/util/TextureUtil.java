package net.alan.gui.util;

import net.minecraft.resources.Identifier;

public class TextureUtil {
    public static Identifier resolveTextureId(String path) {
        if (path == null || path.isEmpty()) return null;
        Identifier id = Identifier.tryParse(path);
        if (id == null) return null;

        String p = id.getPath();
        if (!p.startsWith("textures/")) {
            if (!p.endsWith(".png")) {
                p = p + ".png";
            }
            p = "textures/gui/sprites/" + p;
            return Identifier.fromNamespaceAndPath(id.getNamespace(), p);
        }
        return id;
    }
}
