package de.cypix.tasks_check_bot.reminder;

public class ReminderTask {

    private int userId;
    private long discordId;
    private int timeBefore;
    private TimeUnit timeUnit;

    public ReminderTask(int userId, long discordId, int timeBefore, TimeUnit timeUnit) {
        this.userId = userId;
        this.discordId = discordId;
        this.timeBefore = timeBefore;
        this.timeUnit = timeUnit;
    }

    @Deprecated
    public ReminderTask(int userId, int timeBefore, TimeUnit timeUnit) {
        this.userId = userId;
        this.timeBefore = timeBefore;
        this.timeUnit = timeUnit;
    }

    public int getUserId() {
        return userId;
    }

    public int getTimeBefore() {
        return timeBefore;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public long getDiscordId() {
        return discordId;
    }
}
