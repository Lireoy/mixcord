# Mixcord

A Discord bot which delivers notifications about Mixer streamers, made with Java and JDA using RethinkDB.
You can view Mixcord's website [here](https://streamcord.io/mixer/).

# Requirements to run
* Java JRE 1.8.0_231 (for development JDK 1.8.0_231)
* RethinkDB 2.3.6 or above
* credentials.json (Discord API token & Mixer API Client ID is a must)
* ASCII art (optional)

# How to run
1) Clone this repo
2) Open with IntelliJ IDEA
3) Customize `credentials.json` with your own API keys & stuff.
4) Run the following Maven goals: Clean, Install
5) Add a Build & Run configuration in IntelliJ with the following if it's not present:
    1) Set the working directory to the cloned folder
    2) Select the main class: bot.Mixcord
    3) Use VM Options: -Xmx800m -Xms800m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 
        * (800m is the amount of memory used)
    4) Set use classpath of module: Mixcord
    5) Set JRE to 1.8.0_231 (the name of your 1.8 SDK, however you defined it for IntelliJ)


#### Not requirement but good to know
> Logger outputs to both Console and File. All logging will be redirected to a file `logs/app.log`. Furthermore, this log file will be archived daily or when the file size is **larger than 10MB**. Total size of all archives is **4GB**. Old archives will be deleted if limit is exceeded. All log files are kept for **7 days**

## Dependencies
*This might get outdated as development goes.*
* JDA 4.1.1_109
* JDA Utilities 3.0.2
* RethinkDB Java Driver 2.3.3
* Apache HttpComponents 4.5.11
* Org.JSON 20190722
* Lombok API 1.18.12
* Logback Classic 1.2.3
* Google Gson 2.8.6
* Maven Compiler Plugin 3.8.1
* Maven Jar Plugin 3.2.0


## Commands
### Informative
Info, Invite, Ping, WhoCanUseMe

### Mixer
MixerUser, MixerUserSocials

### Notifications
AddNotif, DeleteNotif, ChannelNotifs, ServerNotifs, MakeDefault,
NotifDetails, NotifPreview, NotifColorEdit, NotifEmbedConfig, NotifMesageEdit

### Owner
StartNotifService, StopNotifService, NotifServiceStatus,
RoleInfo, ServerInfo, Whitelist, Shutdown