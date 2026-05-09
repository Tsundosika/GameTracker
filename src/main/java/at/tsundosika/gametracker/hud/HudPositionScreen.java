package at.tsundosika.gametracker.hud;

import at.tsundosika.gametracker.config.GametrackerConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudPositionScreen extends Screen {

    private boolean dragging = false;
    private int lastMouseX;
    private int lastMouseY;

    public HudPositionScreen() {
        super(Component.literal("GameTracker - Move HUD"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        guiGraphics.fill(0, 0, width, height, 0x88000000);

        GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
        TrackerHUD.renderContent(guiGraphics, minecraft, config.hudX, config.hudY, true);

        guiGraphics.drawCenteredString(
            minecraft.font,
            Component.literal("Drag to reposition  |  Escape to save").withStyle(ChatFormatting.YELLOW),
            width / 2, height - 20, 0xFFFFFFFF
        );

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean fromKeyboard) {
        if (event.button() == 0) {
            GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
            int hx = config.hudX - TrackerHUD.PADDING;
            int hy = config.hudY - TrackerHUD.PADDING;
            int hw = TrackerHUD.getWidth(minecraft);
            int hh = TrackerHUD.getHeight(minecraft);

            if (lastMouseX >= hx && lastMouseX <= hx + hw && lastMouseY >= hy && lastMouseY <= hy + hh) {
                dragging = true;
                return true;
            }
        }
        return super.mouseClicked(event, fromKeyboard);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (dragging && event.button() == 0) {
            GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
            config.hudX = Math.max(0, Math.min(width - TrackerHUD.getWidth(minecraft), config.hudX + (int) deltaX));
            config.hudY = Math.max(0, Math.min(height - TrackerHUD.getHeight(minecraft), config.hudY + (int) deltaY));
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            dragging = false;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        AutoConfig.getConfigHolder(GametrackerConfig.class).save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
