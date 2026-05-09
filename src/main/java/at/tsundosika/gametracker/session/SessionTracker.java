package at.tsundosika.gametracker.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionTracker {

    private static final SessionTracker INSTANCE = new SessionTracker();

    private final List<RoundEntry> rounds = new ArrayList<>();
    private String currentMode = "Unknown";

    private SessionTracker() {}

    public static SessionTracker getInstance() {
        return INSTANCE;
    }

    public void recordResult(RoundResult result) {
        rounds.add(new RoundEntry(currentMode, result));
    }

    public void setCurrentMode(String mode) {
        this.currentMode = mode;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public int getTotalWins() {
        return (int) rounds.stream().filter(r -> r.result() == RoundResult.WIN).count();
    }

    public int getTotalLosses() {
        return (int) rounds.stream().filter(r -> r.result() == RoundResult.LOSS).count();
    }

    public int getTotalRounds() {
        return rounds.size();
    }

    public Map<String, long[]> getStatsByMode() {
        return rounds.stream().collect(Collectors.groupingBy(
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

    public List<RoundEntry> getRounds() {
        return List.copyOf(rounds);
    }

    public void reset() {
        rounds.clear();
        currentMode = "Unknown";
    }
}
