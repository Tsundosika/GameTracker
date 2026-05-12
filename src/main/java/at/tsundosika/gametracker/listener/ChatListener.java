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
    private static final Pattern RESIGN_PATTERN = Pattern.compile("([A-Za-z0-9_]+)\\s+resigned");

    public static void register() {
        ClientReceiveMessageEvents.GAME.register(ChatListener::onGameMessage);
        ClientReceiveMessageEvents.CHAT.register(
            (message, signedMessage, sender, params, receptionTimestamp) -> onGameMessage(message, false)
        );
    }

    private static void onGameMessage(Component message, boolean overlay) {
        if (overlay) return;

        String raw = message.getString();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        String localName = minecraft.player.getGameProfile().name();

        if (raw.contains("Match Complete")) {
            Matcher matcher = WINNER_PATTERN.matcher(raw);
            if (!matcher.find()) return;

            String winner = matcher.group(1);
            if (winner.equalsIgnoreCase(localName)) {
                SessionTracker.getInstance().recordResult(RoundResult.WIN);
            } else {
                SessionTracker.getInstance().recordResult(RoundResult.LOSS);
            }
            return;
        }

        Matcher resignMatcher = RESIGN_PATTERN.matcher(raw);
        if (resignMatcher.find()) {
            String resigner = resignMatcher.group(1);
            if (resigner.equalsIgnoreCase(localName)) {
                SessionTracker.getInstance().recordResult(RoundResult.LOSS);
            } else {
                SessionTracker.getInstance().recordResult(RoundResult.WIN);
            }
        }
    }
}
