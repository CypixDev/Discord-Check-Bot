package de.cypix.tasks_check_bot.scheduler;


import java.util.TimerTask;

public class CheckScheduler extends TimerTask {

    private int i; //in sec
    private Runnable runnable;

    public CheckScheduler(Runnable runnable, int i) {
        this.i = i;
        this.runnable = runnable;
        run();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(i* 1000L);
            runnable.run();
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
