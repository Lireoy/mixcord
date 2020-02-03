package bot.structure;

public enum CommandCategory {

    INFORMATIVE("Informative"),
    MIXER("Mixer"),
    NOTIFICATIONS("Notifications"),
    OWNER("Owner");

    private final String text;

    CommandCategory(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
