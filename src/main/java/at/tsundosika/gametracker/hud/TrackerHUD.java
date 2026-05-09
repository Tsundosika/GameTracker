package at.tsundosika.gametracker.hud;

import at.tsundosika.gametracker.config.GametrackerConfig;
import at.tsundosika.gametracker.session.SessionTracker;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackerHUD {

    static final int PADDING = 3;

    public static void register() {
        HudRenderCallback.EVENT.register(TrackerHUD::onHudRender);
    }

    private static void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
        if (!config.hudEnabled) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) return;

        renderContent(guiGraphics, minecraft, config.hudX, config.hudY, false);
    }

    public static void renderContent(GuiGraphics guiGraphics, Minecraft minecraft, int x, int y, boolean highlighted) {
        GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
        List<Component> lines = buildLines(SessionTracker.getInstance(), config);

        int lineHeight = minecraft.font.lineHeight + 2;
        int maxWidth = lines.stream().mapToInt(minecraft.font::width).max().orElse(80);
        int totalHeight = lines.size() * lineHeight;

        int bx = x - PADDING;
        int by = y - PADDING;
        int bw = maxWidth + PADDING * 2;
        int bh = totalHeight + PADDING * 2;

        guiGraphics.fill(bx, by, bx + bw, by + bh, highlighted ? 0xCC000000 : 0x88000000);

        if (highlighted) {
            guiGraphics.fill(bx,          by,          bx + bw,     by + 1,      0xFFFFFFFF);
            guiGraphics.fill(bx,          by + bh - 1, bx + bw,     by + bh,     0xFFFFFFFF);
            guiGraphics.fill(bx,          by,          bx + 1,      by + bh,     0xFFFFFFFF);
            guiGraphics.fill(bx + bw - 1, by,          bx + bw,     by + bh,     0xFFFFFFFF);
        }

        int currentY = y;
        for (Component line : lines) {
            guiGraphics.drawString(minecraft.font, line, x, currentY, 0xFFFFFFFF, true);
            currentY += lineHeight;
        }
    }

    public static int getWidth(Minecraft minecraft) {
        GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
        List<Component> lines = buildLines(SessionTracker.getInstance(), config);
        return lines.stream().mapToInt(minecraft.font::width).max().orElse(80) + PADDING * 2;
    }

    public static int getHeight(Minecraft minecraft) {
        GametrackerConfig config = AutoConfig.getConfigHolder(GametrackerConfig.class).getConfig();
        List<Component> lines = buildLines(SessionTracker.getInstance(), config);
        return lines.size() * (minecraft.font.lineHeight + 2) + PADDING * 2;
    }

    static List<Component> buildLines(SessionTracker tracker, GametrackerConfig config) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.literal("GameTracker").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        lines.add(
            Component.literal("Wins: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(tracker.getTotalWins())).withStyle(ChatFormatting.WHITE))
        );
        lines.add(
            Component.literal("Losses: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.valueOf(tracker.getTotalLosses())).withStyle(ChatFormatting.WHITE))
        );
        lines.add(
            Component.literal("Mode: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(tracker.getCurrentMode()).withStyle(ChatFormatting.WHITE))
        );

        if (config.showPerMode) {
            Map<String, long[]> byMode = tracker.getStatsByMode();
            if (!byMode.isEmpty()) {
                lines.add(Component.literal("--- Per Mode ---").withStyle(ChatFormatting.DARK_GRAY));
                byMode.forEach((mode, wl) -> {
                    MutableComponent entry = Component.literal(mode).withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(wl[0] + "W").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("/").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(wl[1] + "L").withStyle(ChatFormatting.RED));
                    lines.add(entry);
                });
            }
        }

        return lines;
    }
}
