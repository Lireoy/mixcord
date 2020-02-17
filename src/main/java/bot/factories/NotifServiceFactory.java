package bot.factories;

import bot.services.NotifService;

public class NotifServiceFactory {

    private static NotifService notifService;

    private NotifServiceFactory() {
        notifService = new NotifService();
    }

    public static NotifService getNotifService() {
        if (notifService == null) {
            new NotifServiceFactory();
        }

        return notifService;
    }
}
