# MixerBot

Hi! This is Lireoy, the developer of MixerBot. You can read important details here.
You can view MixerBot's website [here](https://mixerbot.io/), which was made by Akira.

# Requirements to run
* **RethinkDB 2.3.6** or above
* **.ENV** file in the same directory as MixerBot.jar
* RethinkDB client driver connection can be customized in the **.ENV file**. Default: 127.0.0.1:28015
* RethinkDB database and table names can be customized in the **.ENV file**.
* **Java** SE Runtime Environment **1.8.0_191** or above
* Internet connection
* Valid **Discord API** token
* Valid **Mixer API Client ID and Secret***
	* *Optional* ASCII art in the same directory as MixerBot.jar
**If not provided application will fail with an Exception, but carry on (in theory)* 
	* *Optional* Guild and Channel for posting metrics can be customized in the **.ENV file**. 
**Metrics posting has a fallback Guild and Channel predefined.

#### Not requirement but good to know
> Logger outputs to both Console and File. All logging will be redirected to a file `logs/app.log`. Furthermore, this log file will be archived daily or when the file size is **larger than 10MB**. Total size of all archives is **4GB**. Old archives will be deleted if limit is exceeded. All log files are kept for **7 days**

## Supported commands
#### Generic
* Shutdown
**Limited to owners only*

#### Informative
* Info
* Invite
* Ping
* RoleInfo
* ServerInfo

#### Mixer
* MixerUser
* MixerUserSocials

#### Notifications
* AddNotif
* DeleteNotif
* ChannelNotifs
* ServerNotifs
* StartNotifService
**Limited to owners only*
* StopNotifService
**Limited to owners only*

## Other fun details
*This might get outdated as development goes.*
* JDA 4.0.0_56
* JDA Utilities 3.0.2
* RethinkDB Java Driver 2.3.3
* Apache HttpComponents 4.5.10
* Lombok API 1.18.10
* Logback Classic 1.2.3
* Java .Env 5.1.3
* Maven Compiler Plugin 3.8.1
* Maven Jar Plugin 3.1.2
