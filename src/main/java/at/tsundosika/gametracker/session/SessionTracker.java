package at.tsundosika.gametracker.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionTracker {

    private static final SessionTracker INSTANCE = new SessionTracker();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path STATS_FILE = FabricLoader.getInstance().getConfigDir().resolve("gametracker_alltime.json");

    private final List<RoundEntry> sessionRounds = new ArrayList<>();
    private int allTimeWins = 0;
    private int allTimeLosses = 0;
    private String currentMode = "Unknown";

    private SessionTracker() {}

    public static SessionTracker getInstance() {
        return INSTANCE;
    }

    public void recordResult(RoundResult result) {
        sessionRounds.add(new RoundEntry(currentMode, result));
        if (result == RoundResult.WIN) {
            allTimeWins++;
        } else {
            allTimeLosses++;
        }
        saveAllTime();
    }

    public void setCurrentMode(String mode) {
        this.currentMode = mode;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public void resetServerSession() {
        sessionRounds.clear();
    }

    public void resetAllTime() {
        allTimeWins = 0;
        allTimeLosses = 0;
        saveAllTime();
    }

    public int getSessionWins() {
        return (int) sessionRounds.stream().filter(r -> r.result() == RoundResult.WIN).count();
    }

    public int getSessionLosses() {
        return (int) sessionRounds.stream().filter(r -> r.result() == RoundResult.LOSS).count();
    }

    public Map<String, long[]> getSessionStatsByMode() {
        return sessionRounds.stream().collect(Collectors.groupingBy(
            RoundEntry::mode,
            Collectors.collectingAndThen(
                Collectors.toList(),
                list -> new long[]{
                    list.stream().filter(r -> r.result() == RoundResult.WIN).count(),
                    list.stream().filter(r -> r.result() == RoundResult.LOSS).count()
                }
            )
        ));
    }

    public int getAllTimeWins() {
        return allTimeWins;
    }

    public int getAllTimeLosses() {
        return allTimeLosses;
    }

    public void loadAllTime() {
        if (!Files.exists(STATS_FILE)) return;
        try (Reader reader = Files.newBufferedReader(STATS_FILE)) {
            JsonObject obj = GSON.fromJson(reader, JsonObject.class);
            if (obj != null) {
                allTimeWins = obj.has("wins") ? obj.get("wins").getAsInt() : 0;
                allTimeLosses = obj.has("losses") ? obj.get("losses").getAsInt() : 0;
            }
        } catch (IOException e) {
            allTimeWins = 0;
            allTimeLosses = 0;
        }
    }

    private void saveAllTime() {
        JsonObject obj = new JsonObject();
        obj.addProperty("wins", allTimeWins);
        obj.addProperty("losses", allTimeLosses);
        try (Writer writer = Files.newBufferedWriter(STATS_FILE)) {
            GSON.toJson(obj, writer);
        } catch (IOException ignored) {}
    }
}
