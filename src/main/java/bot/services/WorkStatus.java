package bot.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkStatus {

    private static WorkStatus instance;
    private volatile boolean running;

    private WorkStatus() {
        log.info("New WorkStatus");
        this.running = false;
    }

    public static WorkStatus getInstance() {
        if (instance == null)
            instance = new WorkStatus();

        return instance;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void markStarted() {
        log.info("WorkStatus marked as started.");
        this.running = true;
    }

    public synchronized void markFinished() {
        log.info("WorkStatus marked as finished.");
        this.running = false;
    }
}
