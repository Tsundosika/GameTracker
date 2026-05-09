package at.tsundosika.gametracker.listener;

import at.tsundosika.gametracker.session.SessionTracker;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;

public class ScoreboardListener {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null || client.player == null) return;

            Objective objective = client.level.getScoreboard()
                    .getDisplayObjective(DisplaySlot.SIDEBAR);

            if (objective != null) {
                String title = objective.getDisplayName().getString();
                if (!title.isBlank()) {
                    SessionTracker.getInstance().setCurrentMode(title);
                }
            }
        });
    }
}
