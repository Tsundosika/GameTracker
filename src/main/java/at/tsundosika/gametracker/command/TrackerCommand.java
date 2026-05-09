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
                    .then(literal("reset").executes(TrackerCommand::executeReset))
                    .then(literal("stats").executes(TrackerCommand::executeStats))
            )
        );
    }

    private static int executeReset(CommandContext<FabricClientCommandSource> ctx) {
        SessionTracker.getInstance().reset();
        ctx.getSource().sendFeedback(Component.literal("Session reset.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int executeStats(CommandContext<FabricClientCommandSource> ctx) {
        SessionTracker tracker = SessionTracker.getInstance();
        ctx.getSource().sendFeedback(
            Component.literal("GameTracker Stats").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );
        ctx.getSource().sendFeedback(
            Component.literal("Wins: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal(String.valueOf(tracker.getTotalWins())).withStyle(ChatFormatting.WHITE))
        );
        ctx.getSource().sendFeedback(
            Component.literal("Losses: ").withStyle(ChatFormatting.RED)
                .append(Component.literal(String.valueOf(tracker.getTotalLosses())).withStyle(ChatFormatting.WHITE))
        );

        Map<String, long[]> byMode = tracker.getStatsByMode();
        if (!byMode.isEmpty()) {
            ctx.getSource().sendFeedback(Component.literal("--- Per Mode ---").withStyle(ChatFormatting.GRAY));
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
