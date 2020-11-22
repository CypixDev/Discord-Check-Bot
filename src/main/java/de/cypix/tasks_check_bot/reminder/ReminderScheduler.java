package de.cypix.tasks_check_bot.reminder;

import java.util.TimerTask;

public class ReminderScheduler extends TimerTask {

    private int i; //in sec
    private Runnable runnable;

    public ReminderScheduler(Runnable runnable, int i) {
        this.i = i;
        this.runnable = runnable;
        run();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(i*1000);
            runnable.run();
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }
}
