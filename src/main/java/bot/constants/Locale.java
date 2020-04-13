package bot.constants;

import java.util.HashMap;
import java.util.Map;

public class Locale {

    // CATEGORIES
    public static final Map<String, String> CATEGORIES = new HashMap<String, String>() {{
        put("INFORMATIVE", "Informative");
        put("MIXER", "Mixer");
        put("NOTIFICATIONS", "Notifications");
        put("OWNER", "Owner");
    }};

    // INFO COMMAND
    public static final String INFO_COMMAND_HELP = "Shows information about the bot.";
    public static final String INFO_COMMAND_UPTIME_TITLE = "Uptime";
    public static final String INFO_COMMAND_UPTIME = "%s days, %s hours, %s minutes, %s seconds";
    public static final String INFO_COMMAND_USAGE_TITLE = "Usage";
    public static final String INFO_COMMAND_USAGE = "· %s servers\n· %s members";
    public static final String INFO_COMMAND_JAVA_VERSION_TITLE = "Version";
    public static final String INFO_COMMAND_JAVA_VERSION = "· Java %s";
    public static final String INFO_COMMAND_SHARDS_TITLE = "Shards";
    public static final String INFO_COMMAND_SHARDS = "· Current shard: %s\n· Shard latency: %sms\n· Total shards: %s";
    public static final String INFO_COMMAND_RAM_USAGE_TITLE = "System";
    public static final String INFO_COMMAND_RAM_USAGE = "%sMB / %sMB";
    public static final String INFO_COMMAND_LINKS_TITLE = "Links";
    public static final String INFO_COMMAND_LINKS = "· Website: %s\n· Discord: %s";
    public static final String INFO_COMMAND_DEVELOPER_TITLE = "Developer";
    public static final String INFO_COMMAND_DEVELOPER = "Lireoy#4444";
    public static final String INFO_COMMAND_INFRASTRUCTURE_TITLE = "Infrastructure";
    public static final String INFO_COMMAND_INFRASTRUCTURE = "Provided by Akira#8185";

    // INVITE COMMAND
    public static final String INVITE_COMMAND_HELP = "Provides an invite link so you can add this bot to your server.";
    public static final String INVITE_COMMAND_INVITE_TITLE = "Invite";
    public static final String INVITE_COMMAND_INVITE_LINK = "[Click Here to Invite Me](https://discordapp.com/oauth2/authorize?client_id=%s&permissions=347200&scope=bot)";

    // PING COMMAND
    public static final String PING_COMMAND_HELP = "Shows the current latency of the bot.";
    public static final String PING_COMMAND_CALCULATING = "Calculating...";
    public static final String PING_COMMAND_REPLY = "Ping: %sms | Websocket: %sms";

    // WHO CAN USE ME COMMAND
    public static final String WHO_CAN_USE_ME_COMMAND_HELP = "Displays those roles which can use Mixcord.";
    public static final String WHO_CAN_USE_ME_COMMAND_NO_ROLES = "There are no roles which can use Mixcord.";
    public static final String WHO_CAN_USE_ME_COMMAND_MANAGE_NOTIFS = "Roles which can manage notifications:\n\n";
    public static final String WHO_CAN_USE_ME_COMMAND_SIMPLE_LINE = "· <@&%s>\n";
    public static final String WHO_CAN_USE_ME_COMMAND_ADVANCED_LINE = "· <@&%s> (Managed by an integration)\n";
    public static final String WHO_CAN_USE_ME_COMMAND_BASIC_COMMANDS = "Roles which can use basic commands:\n\n";

    // MIXER USER COMMAND
    public static final String MIXER_USER_COMMAND_HELP = "Displays a Mixer user's data.";
    public static final String MIXER_USER_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String MIXER_USER_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String MIXER_USER_COMMAND_JSON_WAS_NULL = "Query response JSON was null, when requesting data for a user, please contact the developer: **Lireoy#4444**";
    public static final String MIXER_USER_COMMAND_NO_SUCH_STREAMER = "There is no such streamer...";
    public static final String MIXER_USER_COMMAND_TRUSTED = "Verified: %s\nPartnered: %s\n";
    public static final String MIXER_USER_COMMAND_STATUS = "Online: %s\nFeatured: %s";
    public static final String MIXER_USER_COMMAND_NO_FOLLOWERS = "No followers.";
    public static final String MIXER_USER_COMMAND_NO_STREAM_TITLE = "No stream title available.";
    public static final String MIXER_USER_COMMAND_NO_GAME = "No game available.";
    public static final String MIXER_USER_COMMAND_NO_LANGUAGE = "No language available.";
    public static final String MIXER_USER_COMMAND_NO_AUIDIENCE = "No audience available.";
    public static final String MIXER_USER_COMMAND_LIVE_STREAM_LINK = "[Click here to watch on Mixer](%s)";
    public static final String MIXER_USER_COMMAND_BIO_TITLE = "Bio";
    public static final String MIXER_USER_COMMAND_TRUSTED_TITLE = "Trusted";
    public static final String MIXER_USER_COMMAND_STATUS_TITLE = "Status";
    public static final String MIXER_USER_COMMAND_FOLLOWERS_TITLE = "Followers";
    public static final String MIXER_USER_COMMAND_CURRENTLY_LIVE_TITLE = "Currently live";
    public static final String MIXER_USER_COMMAND_GAME_TITLE = "Game";
    public static final String MIXER_USER_COMMAND_VIEWERS_TITLE = "Viewers";
    public static final String MIXER_USER_COMMAND_LANGUAGE_TITLE = "Language";
    public static final String MIXER_USER_COMMAND_TARGET_AUDIENCE_TITLE = "Target audience";
    public static final String MIXER_USER_COMMAND_LINK_TITLE = "Link";

    // MIXER USER SOCIALS COMMAND
    public static final String MIXER_USER_SOCIALS_COMMAND_HELP = "Displays a Mixer user's social profiles.";
    public static final String MIXER_USER_SOCIALS_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String MIXER_USER_SOCIALS_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String MIXER_USER_SOCIALS_COMMAND_JSON_WAS_NULL = "Query response JSON was null, when requesting data for a user, please contact the developer: **Lireoy#4444**";
    public static final String MIXER_USER_SOCIALS_COMMAND_NO_SUCH_STREAMER = "There is no such streamer...";
    public static final String MIXER_USER_SOCIALS_COMMAND_FACEBOOK = "· [Facebook](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_INSTAGRAM = "· [Instagram](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_TWITTER = "· [Twitter](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_YOUTUBE = "· [Youtube](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_DISCORD = "· [Discord](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_PATREON = "· [Patreon](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_PLAYER = "· [Player](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_SOUNDCLOUD = "· [Soundcloud](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_STEAM = "· [Steam](%s)\n";
    public static final String MIXER_USER_SOCIALS_COMMAND_NO_SOCIALS = "No socials are available.";

    // ADD NOTIF COMMAND
    public static final String ADD_NOTIF_COMMAND_HELP = "Creates a new notification for a Mixer streamer in the channel where the command is used.";
    public static final String ADD_NOTIF_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String ADD_NOTIF_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String ADD_NOTIF_COMMAND_SERVER_DOES_NOT_EXIST = "This server does not exist in the database. Please contact the developer: **Lireoy#4444**";
    public static final String ADD_NOTIF_COMMAND_FREE_LIMIT_REACHED = "This server has reached the limit for the number of notifications.";
    public static final String ADD_NOTIF_COMMAND_TIER_ONE_LIMIT_REACHED = "This server has reached the limit for the number of notifications.";
    public static final String ADD_NOTIF_COMMAND_JSON_WAS_NULL = "Query response JSON was null, when requesting data for a user, please contact the developer: **Lireoy#4444**";
    public static final String ADD_NOTIF_COMMAND_NO_SUCH_STREAMER = "There is no such streamer...";
    public static final String ADD_NOTIF_COMMAND_EMPTY_STREAMER = "Streamer name or ID is empty. Please contact the developer: **Lireoy#4444**";
    public static final String ADD_NOTIF_COMMAND_SUCCESSFUL = "From now, you will receive notifications for %s in this channel.";
    public static final String ADD_NOTIF_COMMAND_ALREADY_EXISTS = "You have already set up a notification for this streamer.";

    // DELETE NOTIF COMMAND
    public static final String DELETE_NOTIF_COMMAND_HELP = "Deletes a streamer notification in the channel where the command is used.";
    public static final String DELETE_NOTIF_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String DELETE_NOTIF_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String DELETE_NOTIF_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification...";
    public static final String DELETE_NOTIF_COMMAND_SUCCESSFUL = "Notification was deleted.";

    // CHANNEL NOTIFS
    public static final String CHANNEL_NOTIFS_COMMAND_HELP = "Lists all available notifications for this channel.";
    public static final String CHANNEL_NOTIFS_COMMAND_NO_NOTIFICATIONS = "There are no notifications in this channel";
    public static final String CHANNEL_NOTIFS_COMMAND_LINE = "· [%s](%s%s)\n";
    public static final String CHANNEL_NOTIFS_COMMAND_ONLY_ONE = "There's only 1 notification in this channel.";
    public static final String CHANNEL_NOTIFS_COMMAND_N_AMOUNT = "There's a total of %s notifications in this channel.";
    public static final String CHANNEL_NOTIFS_COMMAND_CHANNEL_NOTIFS_TITLE = "Channel Notifications";

    // SERVER NOTIFS
    public static final String SERVER_NOTIFS_COMMAND_HELP = "Lists all available notifications for this server.";
    public static final String SERVER_NOTIFS_COMMAND_NO_NOTIFICATIONS = "There are no notifications in this server.";
    public static final String SERVER_NOTIFS_COMMAND_CHANNEL_LINE = "\n<#%s>\n";
    public static final String SERVER_NOTIFS_COMMAND_STREAMER_LINE = "· [%s](%s%s)\n";
    public static final String SERVER_NOTIFS_COMMAND_ONLY_ONE = "There's only 1 notification in this server.";
    public static final String SERVER_NOTIFS_COMMAND_N_AMOUNT = "There's a total of %s notifications in this server.";
    public static final String SERVER_NOTIFS_COMMAND_SERVER_NOTIFS_TITLE = "Server Notifications";


    // MAKE DEFAULT COMMAND
    public static final String MAKE_DEFAULT_COMMAND_HELP = "Resets a notification's configuration to the defaults.";
    public static final String MAKE_DEFAULT_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String MAKE_DEFAULT_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String MAKE_DEFAULT_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification in this channel.";
    public static final String MAKE_DEFAULT_COMMAND_SUCCESSFUL = "Notification configuration was reset for `%s`.";


    public static final String NOTIF_DETAILS_COMMAND_HELP = "Lists all settings for a notifications.";
    public static final String NOTIF_DETAILS_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String NOTIF_DETAILS_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String NOTIF_DETAILS_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification...";
    public static final String NOTIF_DETAILS_COMMAND_NOTIFICATION_DETAILS_TITLE = "Notification details";
    public static final String NOTIF_DETAILS_COMMAND_NAME_TITLE = "Name";
    public static final String NOTIF_DETAILS_COMMAND_SEND_EMBED_TITLE = "Send in embed";
    public static final String NOTIF_DETAILS_COMMAND_EMBED_COLOR_TITLE = "Embed color";
    public static final String NOTIF_DETAILS_COMMAND_EMBED_COLOR = "#%s";
    public static final String NOTIF_DETAILS_COMMAND_START_MESSAGE_TITLE = "Stream start message";
    public static final String NOTIF_DETAILS_COMMAND_END_MESSAGE_TITLE = "Stream end message";

    // NOTIF PREVIEW COMMAND
    public static final String NOTIF_PREVIEW_COMMAND_HELP = "Sends a preview for a notification.";
    public static final String NOTIF_PREVIEW_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String NOTIF_PREVIEW_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String NOTIF_PREVIEW_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification in this channel.";
    public static final String NOTIF_PREVIEW_COMMAND_JSON_WAS_NULL = "Query response JSON was null, when requesting data for a user, please contact the developer: **Lireoy#4444**";
    public static final String NOTIF_PREVIEW_COMMAND_WAS_NOT_FOUND_ON_MIXER = "Streamer was not found on Mixer.";

    // NOTIF MESSAGE EDIT COMMAND
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_HELP = "Edits the notification's message.";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NO_FULL_CONFIG = "Please provide a full configuration. %s";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NO_NEW_MESSAGE = "Please provide a new notification message!";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NEW_MESSAGE_TOO_LONG = "Your new notification message is too long! (max 300 chars)";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification in this channel.";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_SAME_MESSAGE = "Your new message is same as the old one!";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_WRONG_LINK = "Your notification message contains a link to a different streamer.";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NO_LINK = "Your notification message does not contain a link to the streamer.";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_SUCCESSFUL = "Notification message was changed for the following notification: `%s`";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_OLD_MESSAGE = "\nOld message:\n```%s```\n\n";
    public static final String NOTIF_MESSAGE_EDIT_COMMAND_NEW_MESSAGE = "New message:\n```%s```";

    // NOTIF COLOR EDIT COMMAND
    public static final String NOTIF_COLOR_EDIT_COMMAND_HELP = "Edits the notification's embed color.";
    public static final String NOTIF_COLOR_EDIT_COMMAND_NO_FULL_CONFIG = "Please provide a full configuration. %s";
    public static final String NOTIF_COLOR_EDIT_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String NOTIF_COLOR_EDIT_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String NOTIF_COLOR_EDIT_COMMAND_INVALID_HEX = "Please provide a valid hex color.";
    public static final String NOTIF_COLOR_EDIT_COMMAND_NO_NOTIFICATIONS = "There are no notifications in this channel";
    public static final String NOTIF_COLOR_EDIT_COMMAND_SAME_COLOR = "Your new color is same as the old one!";
    public static final String NOTIF_COLOR_EDIT_COMMAND_SUCCESSFUL = "Notification color was changed for the following notification: `%s`";
    public static final String NOTIF_COLOR_EDIT_COMMAND_OLD_COLOR = "\nOld color:\n```%s```\n\n";
    public static final String NOTIF_COLOR_EDIT_COMMAND_NEW_COLOR = "New color:\n```%s```";

    // NOTIF EMBED CONFIG COMMAND
    public static final String NOTIF_EMBED_CONFIG_COMMAND_HELP = "Edits the notification format. Set it to true for embed, false for non-embed notification.";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_NO_FULL_CONFIG = "Please provide a full configuration. %s";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_NO_STREAMER_NAME = "Please provide a streamer name!";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_TOO_LONG_NAME = "This name is too long! Please provide a shorter one!";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_INVALID_EMBED_VALUE = "Please provide a valid embed value. %s";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_NO_SUCH_NOTIFICATION = "There is no such notification in this channel.";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_NO_LINK = "Your notification message does not contain a link to the streamer. Please include one, and try again.";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_ALREADY_SET = "This embed configuration is already set.";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_SUCCESSFUL = "Notification format was changed for the following notification: `%s`";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_SEND_AS_EMBED = "\nThis notification will be sent as an embed in the future.";
    public static final String NOTIF_EMBED_CONFIG_COMMAND_SEND_AS_NON_EMBED = "\nThis notification will be sent without an embed in the future.";


    public static final String DEBUG_COMMAND_HELP = "Helps retrieve information about a server which the bot is in.";
    public static final String DEBUG_COMMAND_NO_ARGUMENTS = "Please provide some arguments.";
    public static final String DEBUG_COMMAND_NO_SERVER_ID = "Empty serverId argument.";
    public static final String DEBUG_COMMAND_NO_CHANNEL_ID = "Empty channelId argument.";
    public static final String DEBUG_COMMAND_NO_STREAMER_NAME = "Empty streamerName argument.";
    public static final String DEBUG_COMMAND_NOT_IN_SERVER = "I'm not in this server.";
    public static final String DEBUG_COMMAND_NO_NOTIFS_IN_SERVER = "There are no notifications in this server.";
    public static final String DEBUG_COMMAND_NO_NOTIFS_IN_CHANNEL = "There are no notifications in this channel.";
    public static final String DEBUG_COMMAND_CHANNEL_LINE = "\n%s - <#%s>\n";
    public static final String DEBUG_COMMAND_STREAMER_LINE = "· [%s](%s%s)\n";
    public static final String DEBUG_COMMAND_SERVER_NOTIFS_TITLE = "Notifications in G:%s";
    public static final String DEBUG_COMMAND_CHANNEL_NOTIFS_TITLE = "Notifications in G:%s C:%s";
    public static final String DEBUG_COMMAND_NO_SUCH_CHANNEL = "There is no such channel.";
    public static final String DEBUG_COMMAND_NO_SUCH_STREAMER = "There is no such notification in this channel";
    public static final String DEBUG_COMMAND_NO_TALK_POWER = "No talk power in that channel.";

    // GET DB STATS COMMAND
    public static final String GET_DB_STATS_COMMAND_HELP = "Shows information about the current database usage.";
    public static final String GET_DB_STATS_COMMAND_STATISTICS = "· Guilds: %s\n· Streamers: %s\n· Notifications: %s";
    public static final String GET_DB_STATS_COMMAND_STATISTICS_TITLE = "Database statistics";

    // GET SERVER SHARD COMMAND
    public static final String GET_SERVER_SHARD_COMMAND_NO_FULL_CONFIG = "Please provide a full configuration.";
    public static final String GET_SERVER_SHARD_COMMAND_NO_SERVER_ID = "Please provide a server ID.";
    public static final String GET_SERVER_SHARD_COMMAND_NO_SHARD_NUMBER = "Please provide the number of shards.";
    public static final String GET_SERVER_SHARD_COMMAND_EXCEPTION = "Number format exception.";

    // NOTIF SERVICE STATUS COMMAND
    public static final String NOTIF_SERVICE_STATUS_COMMAND_HELP = "Creates a new notification for a Mixer streamer in the channel where the command is used.";
    public static final String NOTIF_SERVICE_STATUS_COMMAND_RUNNING = "Notifier service state: RUNNING";
    public static final String NOTIF_SERVICE_STATUS_COMMAND_NOT_RUNNING = "Notifier service state: NOT RUNNING";

    // OWNERS COMMAND
    public static final String OWNERS_COMMAND_HELP = "Shows the owners of this bot.";
    public static final String OWNERS_COMMAND_TITLE = "Owners of this bot are:\n";
    public static final String OWNERS_COMMAND_OWNER_LINE = "· %s#%s\n";
    public static final String OWNERS_COMMAND_NO_OWNER = "There are no owners for this bot.";

    // ROLE INFO COMMAND
    public static final String ROLE_INFO_COMMAND_HELP = "Shows information about a role.";
    public static final String ROLE_INFO_COMMAND_NO_ROLE = "Please provide the name of a role!";
    public static final String ROLE_INFO_COMMAND_NO_SUCH_ROLE = "I couldn't find the role you were looking for!";
    public static final String ROLE_INFO_COMMAND_MULTIPLE_ROLES_FOUND = "Multiple roles found matching `%s`:\n";
    public static final String ROLE_INFO_COMMAND_ROLE_LINE = " - %s (ID: `%s`)\n";
    public static final String ROLE_INFO_COMMAND_REPLy_TITLE = "Information about `%s`:";
    public static final String ROLE_INFO_COMMAND_NONE = "None";
    public static final String ROLE_INFO_COMMAND_PERMISSION_LINE = "`, `%s";
    public static final String ROLE_INFO_COMMAND_ID_TITLE = "ID";
    public static final String ROLE_INFO_COMMAND_CREATION_TITLE = "Creation";
    public static final String ROLE_INFO_COMMAND_COLOR_TITLE = "Color";
    public static final String ROLE_INFO_COMMAND_POSITION_TITLE = "Position";
    public static final String ROLE_INFO_COMMAND_MENTIONABLE_TITLE = "Mentionable";
    public static final String ROLE_INFO_COMMAND_HOISTED_TITLE = "Hoisted";
    public static final String ROLE_INFO_COMMAND_MEMBERS_TITLE = "Members";
    public static final String ROLE_INFO_COMMAND_PERMISSIONS_TITLE = "Permissions";

    // SERVER INFO COMMAND
    public static final String SERVER_INFO_COMMAND_HELP = "Shows information about a server.";
    public static final String SERVER_INFO_COMMAND_GUILD_NULL = "Guild was null.";
    public static final String SERVER_INFO_COMMAND_OWNER_NULL = "Owner was null.";
    public static final String SERVER_INFO_COMMAND_TITLE = "Information about `%s`:";
    public static final String SERVER_INFO_COMMAND_LOCATION = "%s %s";
    public static final String SERVER_INFO_COMMAND_OWNER_LINE = "%s#%s";
    public static final String SERVER_INFO_COMMAND_FEATURES_NONE = "None";
    public static final String SERVER_INFO_COMMAND_FEATURE_LINE = "%s, ";
    public static final String SERVER_INFO_COMMAND_SUMMARY = "%d Online\n%d Idle\n%d Do not Disturb\n%d Offline\nAltogether %d users and %d bots.";
    public static final String SERVER_INFO_COMMAND_ID_TITLE = "ID";
    public static final String SERVER_INFO_COMMAND_OWNER_TITLE = "Owner";
    public static final String SERVER_INFO_COMMAND_LOCATION_TITLE = "Location";
    public static final String SERVER_INFO_COMMAND_CREATION_TITLE = "Creation";
    public static final String SERVER_INFO_COMMAND_FEATURES_TITLE = "Features";
    public static final String SERVER_INFO_COMMAND_MEMBERS_TITLE = "Members";


    // SHUTDOWN COMMAND
    public static final String SHUTDOWN_COMMAND_HELP = "Kills the bot. You know what shutdown means, don't ya? Cool. Be aware.";
    public static final String SHUTDOWN_COMMAND_NO_REASON = "You have to provide a reason.";

    // START NOTIF SERVICE COMMAND
    public static final String START_NOTIF_SERVICE_COMMAND_HELP = "Starts the notifier service.";

    // STOP NOTIF SERVICE COMMAND
    public static final String STOP_NOTIF_SERVICE_COMMAND_HELP = "Stops the notifier service.";

    // WHITELIST COMMAND
    public static final String WHITELIST_COMMAND_HELP = "Add / remove a server from the whitelist, or list all whitelisted servers.";
    public static final String WHITELIST_COMMAND_NO_ARGUMENTS = "Please provide the required parameters.";
    public static final String WHITELIST_COMMAND_NO_FIRST_ARG = "First parameter was empty.\n`%s`";
    public static final String WHITELIST_COMMAND_NO_SECOND_ARG = "Second parameter was empty.\n%s`";
    public static final String WHITELIST_COMMAND_INVALID_SECOND_ARG = "Please provide a valid second arguments. `true` or `false`";
    public static final String WHITELIST_COMMAND_NO_FULL_CONFIG = "Please provide a full configuration.";
    public static final String WHITELIST_COMMAND_OWNER_NOT_AVAILABLE = "(Could not retrieve owner ID)";
    public static final String WHITELIST_COMMAND_NAME_NOT_AVAILABLE = "(Could not retrieve name)";
    public static final String WHITELIST_COMMAND_LINE = "· <@%s> - `%s` - `%s` - `%s members`\n";
    public static final String WHITELIST_COMMAND_NOT_IN_SERVER = "The bot is not in that server.";
    public static final String WHITELIST_COMMAND_DELETED = "Deleted G:`%s` from database.";
    public static final String WHITELIST_COMMAND_ALREADY_SET = "`%s` is already set to `%s`";
    public static final String WHITELIST_COMMAND_UPDATED = "Successfully updated `%s` to `%s`.";
    public static final String WHITELIST_COMMAND_ADDED = "Successfully whitelisted `%s`.";
}
