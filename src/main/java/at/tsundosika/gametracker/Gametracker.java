package at.tsundosika.gametracker;

import at.tsundosika.gametracker.command.TrackerCommand;
import at.tsundosika.gametracker.config.GametrackerConfig;
import at.tsundosika.gametracker.hud.HudPositionScreen;
import at.tsundosika.gametracker.hud.TrackerHUD;
import at.tsundosika.gametracker.listener.ChatListener;
import at.tsundosika.gametracker.listener.ConnectionListener;
import at.tsundosika.gametracker.listener.ScoreboardListener;
import at.tsundosika.gametracker.session.SessionTracker;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Gametracker implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoConfig.register(GametrackerConfig.class, GsonConfigSerializer::new);
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.parse("key.categories.gametracker"));

        KeyMapping moveHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gametracker.move_hud",
                InputConstants.UNKNOWN.getValue(),
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (moveHudKey.consumeClick()) {
                minecraft.setScreen(new HudPositionScreen());
            }
        });

        SessionTracker.getInstance().loadAllTime();
        ChatListener.register();
        ScoreboardListener.register();
        ConnectionListener.register();
        TrackerCommand.register();
        TrackerHUD.register();
    }
}
