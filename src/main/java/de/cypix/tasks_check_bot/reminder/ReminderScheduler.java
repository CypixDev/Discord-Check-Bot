package de.cypix.tasks_check_bot.reminder;

import de.cypix.tasks_check_bot.main.TasksCheckBot;

import java.util.TimerTask;
import java.util.logging.Level;

public class ReminderScheduler extends TimerTask  {

    private int i; //in sec
    private Runnable runnable;

    public ReminderScheduler(Runnable runnable, int i) {
        this.i = i;
        this.runnable = runnable;
        run();
        //IntelliJ
    }

    @Override
    public synchronized void run() {
        try {
            Thread.sleep(i*1000L);
            runnable.run();
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
            TasksCheckBot.logger.log(Level.SEVERE, "REMINDER", e);
        }
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }
}
