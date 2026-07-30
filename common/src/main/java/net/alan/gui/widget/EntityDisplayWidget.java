package net.alan.gui.widget;

import net.alan.gui.context.RenderContext;
import net.alan.gui.data.props.LayoutProps;
import net.alan.gui.entity.FakeLevel;
import net.alan.gui.entity.FakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public class EntityDisplayWidget extends BaseWidget {

    private final String entityType;
    private final float scale;
    private final boolean lookAtMouse;
    private final String animState;
    private final float walkSpeed;
    private final boolean attackEnabled;

    private LivingEntity cachedEntity;
    private boolean triedCache;

    public EntityDisplayWidget(String id, LayoutProps layout, Map<String, String> variables, Map<String, String> member,
                                String entityType, float scale, boolean lookAtMouse,
                                String animState, float walkSpeed, boolean attackEnabled) {
        super(id, layout, variables, member);
        this.entityType = entityType;
        this.scale = scale;
        this.lookAtMouse = lookAtMouse;
        this.animState = animState;
        this.walkSpeed = walkSpeed;
        this.attackEnabled = attackEnabled;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y, int width, int height,
                        RenderContext context, int mouseX, int mouseY, float delta) {
        if (width <= 0 || height <= 0 || !layout.visible()) return;

        RenderContext mergedCtx = mergeContext(context);
        WidgetDimension dim = computeLayout(mergedCtx, width, height);
        Map<String, Integer> vars = buildNumericVars(mergedCtx, width, height, dim.w, dim.h);
        if (!checkCondition(vars)) return;
        int screenX = x + dim.x;
        int screenY = y + dim.y;

        LivingEntity entity = getEntity();
        if (entity == null) return;

        updateEntityAnimation(entity);

        float angleX, angleY;
        if (lookAtMouse) {
            float centerX = (screenX + screenX + dim.w) / 2.0F;
            float centerY = (screenY + screenY + dim.h) / 2.0F;
            angleX = (float) (centerX - mouseX) / (float) dim.w;
            angleY = (float) (centerY - mouseY) / (float) dim.h;
        } else {
            angleX = 0.0f;
            angleY = 0.0f;
        }

        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cameraOrientation = new Quaternionf().rotateX(angleY * 20.0F * (float) (Math.PI / 180.0));
        pose.mul(cameraOrientation);

        float savedBodyRot = entity.yBodyRot;
        float savedBodyRotO = entity.yBodyRotO;
        float savedYRot = entity.getYRot();
        float savedXRot = entity.getXRot();
        float savedXRotO = entity.xRotO;
        float savedHeadRotO = entity.yHeadRotO;
        float savedHeadRot = entity.yHeadRot;

        entity.yBodyRot = 180.0F + angleX * 20.0F;
        entity.yBodyRotO = entity.yBodyRot;
        entity.setYRot(180.0F + angleX * 40.0F);
        entity.xRotO = -angleY * 20.0F;
        entity.setXRot(-angleY * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        float entityScale = entity.getScale();
        float renderScale = (float) ((int) scale) / entityScale;
        Vector3f translate = new Vector3f(0.0F, entity.getBbHeight() / 2.0F, 0.0F);

        float centerXRender = (screenX + screenX + dim.w) / 2.0F;
        float centerYRender = (screenY + screenY + dim.h) / 2.0F;

        // InventoryScreen.renderEntityInInventory(...)

        entity.yBodyRot = savedBodyRot;
        entity.yBodyRotO = savedBodyRotO;
        entity.setYRot(savedYRot);
        entity.xRotO = savedXRotO;
        entity.setXRot(savedXRot);
        entity.yHeadRotO = savedHeadRotO;
        entity.yHeadRot = savedHeadRot;
    }

    private void updateEntityAnimation(LivingEntity entity) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button,
                                RenderContext context, int x, int y, int width, int height) {
        return false;
    }

    private LivingEntity getEntity() {
        if (triedCache) return cachedEntity;
        triedCache = true;

        Minecraft mc = Minecraft.getInstance();
        if ("player".equals(entityType)) {
            if (mc.player != null) {
                cachedEntity = mc.player;
            }
        }
        return cachedEntity;
    }
}