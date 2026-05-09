package at.tsundosika.gametracker.listener;

import at.tsundosika.gametracker.session.SessionTracker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ConnectionListener {

    private static String lastNetworkAddress = null;
    private static boolean pendingReset = false;

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((handler, packetSender, client) -> {
            String address = client.getCurrentServer() != null ? client.getCurrentServer().ip : null;

            if (pendingReset) {
                if (address == null || !address.equals(lastNetworkAddress)) {
                    SessionTracker.getInstance().resetServerSession();
                }
                pendingReset = false;
            }

            lastNetworkAddress = address;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            pendingReset = true;
        });
    }
}
