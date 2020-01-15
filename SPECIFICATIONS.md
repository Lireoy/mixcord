# This document describes the specifications and requirements for features

## Ping
* Should display the WebSocket and total ping.

## Invite
* Should display an embed with an invite link for the bot with the following optional permissions:
    * Read Messages
    * Read Message History
    * Send Messages
    * Embed Links
    * Add Reactions
    * Add External Emojis

## Info command
* Should display the uptime, which the bot was running for so far.
* Should display the total number of guilds, and the total number of users.
* Should display the current version of Java which the bot is running on.
* Should display shard information. (Incomplete)
* Should display RAM usage.
* Should display a link to the Streamcord Mixer site, and a Discord invite link for Mixcord server.
* Should display the developers.

## MixerUser command
There are two different versions for this command. The response is based on the Mixer channel's connectivity status.
When requesting information about an offline channel, the following should be displayed:
* Name
* Bio
* Profile picture used on Mixer as author and thumbnail image
* Verified status
* Partnered status
* Connectivity status (Online)
* Featured status
* Number of followers
* Offline channel banner
 
In case of an online channel, the following should be seen:
* All of the details said for the offline response, plus:
* Current livestream's title
* Current game
* Number of viewers
* Language of the stream
* Target audience
* Link to the stream as a hyperlink
* An image from the stream (Not offline banner)

## MixerUserSocials command
All social media connections are optional, in case of no socials connected, the bot should respond with
`No socials are available.`.
When the response displays connected socials, the following could be displayed:
* Facebook
* Instagram
* Twitter
* YouTube
* Discord
* Patreon
* Player
* Soundcloud
* Steam

All of the socials should be named as provied, and should contain a hyperlink respectively.

# Notifications section

## AddNotif
Required permissions:
* Manage Server
* Message Write
* Message Add Reaction

Streamer name prerequisites
* Should not be empty
* Should not be longer than 20 characters

Other prerequisites
* Server should not have more than 10 notifications

Expected responses:
* Request for streamer name if it's empty
* Streamer name too long
* Confirmation on successful notification creation
* There is already a notification for the given streamer
* There is no such streamer on Mixer
* Server reached the max number of notifications

## DeleteNotif
Required permissions:
* Manage Server
* Message Write
* Message Add Reaction

Streamer name prerequisites
* Should not be empty
* Should not be longer than 20 characters

Expected responses:
* Request for streamer name if it's empty
* Streamer name too long
* Confirmation on successful notification deletion
* There is no such streamer on Mixer
* There is no such streamer in the database

## ChannelNotifs
Required permissions:
* Manage Server
* Message Write

Expected responses
* No notifications in this channel
* A message with the amount of notifications in the channel AND ->
* Embed with all the notifications in the channel, containing hyperlinks for each streamer


## ServerNotifs