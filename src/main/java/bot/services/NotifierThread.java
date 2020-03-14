package bot.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotifierThread {

    private static NotifierThread notifierThread;
    private Thread thread;

    private NotifierThread() {
    }

    public static NotifierThread getInstance() {
        if (notifierThread == null)
            notifierThread = new NotifierThread();

        return notifierThread;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(NotifService.getInstance());
            thread.start();
            log.info("Notifier thread was started.");
        }
    }

    public void stop() {
        if (thread != null) {
            NotifService.getInstance().terminate();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("Notifier thread stopped.");
        }
        thread = null;
    }
}
