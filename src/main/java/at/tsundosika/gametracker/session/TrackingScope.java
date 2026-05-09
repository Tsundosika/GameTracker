package at.tsundosika.gametracker.session;

public enum TrackingScope {
    SESSION("Session"),
    ALL_TIME("All Time");

    private final String displayName;

    TrackingScope(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
