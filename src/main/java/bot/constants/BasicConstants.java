package bot.constants;

public class BasicConstants {

    // BOT
    public static final String PREFIX = ".";
    public static final String SUCCESS = "✅";
    public static final String WARNING = "‼";
    public static final String ERROR = "❌";
    public static final String DISCORD = "https://discord.gg/PXU46xR";
    public static final String MIXCORD_IO = "https://streamcord.io/mixer/";
    public static final String MIXCORD_IO_EMBED_FOOTER = "streamcord.io/mixer";

    // MIXER
    public static final String HTTP_MIXER_COM = "http://mixer.com/";
    public static final String HTTPS_MIXER_COM = "https://mixer.com/";
    public static final String MIXER_THUMB_PRE = "https://thumbs.mixer.com/channel/";
    public static final String MIXER_THUMB_POST = ".big.jpg";
    public static final String MIXER_API_CHANNELS_PATH = "https://mixer.com/api/v1/channels";
    //public static final String MIXER_API_USERS_PATH = "https://mixer.com/api/v1/users";
    //public static final String MIXER_API_WEBHOOK_PATH = "https://mixer.com/api/v1/hooks";
    public static final String MIXER_BANNER_DEFAULT = "https://i.imgur.com/texbqFF.jpg";
    public static final String MIXER_PROFILE_PICTURE_DEFAULT = "https://i.imgur.com/rOGGK8g.png";


    // NOTIFICATION DEFAULT VALUES
    public static final boolean NOTIF_EMBED_DEFAULT = true;
    public static final String NOTIF_EMBED_COLOR_DEFAULT = "ffffff";
    public static final String NOTIF_MESSAGE_DEFAULT = "<https://mixer.com/%s> is now live on Mixer!";
    public static final String NOTIF_END_MESSAGE_DEFAULT = "%s finished streaming.";
    public static final String NOTIF_END_ACTION = "0";
}
