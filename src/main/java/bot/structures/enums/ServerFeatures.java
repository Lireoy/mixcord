package bot.structures.enums;

public enum ServerFeatures {

    ANIMATED_ICON("Animated Icon"),
    BANNER("Banner"),
    COMMERCE("Commerce"),
    DISCOVERABLE("Discoverable"),
    INVITE_SPLASH("Custom Invite Splash"),
    MORE_EMOJI("More than 50 emojis"),
    NEWS("News channel"),
    PARTNERED("Partnered"),
    PUBLIC("Public"),
    VANITY_URL("Custom Invite Link"),
    VERIFIED("Verified"),
    VIP_REGIONS("Has VIP regions available");

    private final String text;

    ServerFeatures(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
