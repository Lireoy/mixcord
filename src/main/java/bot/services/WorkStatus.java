package bot.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WorkStatus {

    private static WorkStatus instance;
    private volatile boolean running;

    private WorkStatus() {
        log.info("WorkStatus class - WorkStatus constructor called");
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
        log.info("WorkStatus class - markStarted()");
        this.running = true;
    }

    public synchronized void markFinished() {
        log.info("WorkStatus class - markFinished()");
        this.running = false;
    }
}
