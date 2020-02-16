package bot.structure.enums;

public enum CommandCategory {

    INFORMATIVE("Informative"),
    MIXER("Mixer"),
    NOTIFICATIONS("Notifications"),
    OWNER("Owner");

    private final String text;

    CommandCategory(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
