package at.tsundosika.gametracker.command;

import at.tsundosika.gametracker.session.SessionTracker;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class TrackerCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(
                literal("tracker")
                    .then(literal("reset")
                        .then(literal("session").executes(TrackerCommand::executeResetSession))
                        .then(literal("alltime").executes(TrackerCommand::executeResetAllTime))
                    )
                    .then(literal("stats").executes(TrackerCommand::executeStats))
            )
        );
    }

    private static int executeResetSession(CommandContext<FabricClientCommandSource> ctx) {
        SessionTracker.getInstance().resetServerSession();
        ctx.getSource().sendFeedback(Component.literal("Session reset.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int executeResetAllTime(CommandContext<FabricClientCommandSource> ctx) {
        SessionTracker.getInstance().resetAllTime();
        ctx.getSource().sendFeedback(Component.literal("All-time stats reset.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int executeStats(CommandContext<FabricClientCommandSource> ctx) {
        SessionTracker tracker = SessionTracker.getInstance();

        ctx.getSource().sendFeedback(
            Component.literal("GameTracker Stats").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );

        ctx.getSource().sendFeedback(
            Component.literal("Session  ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(tracker.getSessionWins() + "W").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(tracker.getSessionLosses() + "L").withStyle(ChatFormatting.RED))
        );

        ctx.getSource().sendFeedback(
            Component.literal("All Time ").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal(tracker.getAllTimeWins() + "W").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(tracker.getAllTimeLosses() + "L").withStyle(ChatFormatting.RED))
        );

        Map<String, long[]> byMode = tracker.getSessionStatsByMode();
        if (!byMode.isEmpty()) {
            ctx.getSource().sendFeedback(Component.literal("--- Session Per Mode ---").withStyle(ChatFormatting.GRAY));
            byMode.forEach((mode, wl) ->
                ctx.getSource().sendFeedback(
                    Component.literal(mode).withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(wl[0] + "W").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(wl[1] + "L").withStyle(ChatFormatting.RED))
                )
            );
        }

        return 1;
    }
}
