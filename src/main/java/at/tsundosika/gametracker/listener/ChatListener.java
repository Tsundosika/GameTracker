package at.tsundosika.gametracker.listener;

import at.tsundosika.gametracker.session.RoundResult;
import at.tsundosika.gametracker.session.SessionTracker;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener {

    private static final Pattern WINNER_PATTERN = Pattern.compile("([A-Za-z0-9_]+)\\s+won the round!");

    public static void register() {
        ClientReceiveMessageEvents.GAME.register(ChatListener::onGameMessage);
        ClientReceiveMessageEvents.CHAT.register(
            (message, signedMessage, sender, params, receptionTimestamp) -> onGameMessage(message, false)
        );
    }

    private static void onGameMessage(Component message, boolean overlay) {
        if (overlay) return;

        String raw = message.getString();

        if (!raw.contains("Match Complete")) return;

        Matcher matcher = WINNER_PATTERN.matcher(raw);
        if (!matcher.find()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        String localName = minecraft.player.getGameProfile().name();
        String winner = matcher.group(1);

        if (winner.equalsIgnoreCase(localName)) {
            SessionTracker.getInstance().recordResult(RoundResult.WIN);
        } else {
            SessionTracker.getInstance().recordResult(RoundResult.LOSS);
        }
    }
}
