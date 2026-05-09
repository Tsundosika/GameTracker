package at.tsundosika.gametracker.config;

public enum HudDisplayMode {
    SESSION("Session Only"),
    ALL_TIME("All Time Only"),
    BOTH("Both");

    private final String displayName;

    HudDisplayMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
